package uz.angrykitten.uybek.ui.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import uz.angrykitten.uybek.ui.navigation.Screen
import uz.angrykitten.uybek.ui.theme.*
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel
import uz.angrykitten.uybek.ui.viewmodel.PhoneStep

// Replace with your real Web Client ID from Firebase Console → Authentication → Google

// ─── Bg color matching reference ─────────────────────────────────────────────
private val AuthBg = Color(0xFFF5F5F8)

// ════════════════════════════════════════════════════════════════════════════
// LOGIN SCREEN
// ════════════════════════════════════════════════════════════════════════════
@Composable
fun LoginScreen(viewModel: AppViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(AuthTab.EMAIL) }

    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState.error) {
        authState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearAuthError() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AuthBg
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(AuthBg)
                .verticalScroll(rememberScrollState())
                .padding(pv)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo
            Box(
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brand),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(24.dp))

            Text("Xush kelibsiz!", fontWeight = FontWeight.Bold, fontSize = 28.sp,
                color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(4.dp))
            Text("Email yoki telefon bilan kiring", color = Color(0xFF8A8A9A), fontSize = 15.sp)
            Spacer(Modifier.height(32.dp))

            // Tab selector
            AuthTabRow(selected = activeTab, onSelect = { activeTab = it; viewModel.resetPhoneStep() })
            Spacer(Modifier.height(24.dp))

            AnimatedContent(targetState = activeTab, label = "tab") { tab ->
                when (tab) {
                    AuthTab.EMAIL -> Column {
                        AuthField(value = email, onChange = { email = it }, label = "Email",
                            icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
                        Spacer(Modifier.height(16.dp))
                        AuthField(value = password, onChange = { password = it }, label = "Parol",
                            icon = Icons.Default.Lock, keyboardType = KeyboardType.Password,
                            isPassword = true, passwordVisible = passwordVisible,
                            onToggle = { passwordVisible = !passwordVisible })
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                if (email.isNotBlank()) viewModel.sendPasswordReset(email) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (it) "Tiklash xati yuborildi ✓" else "Email topilmadi"
                                        )
                                    }
                                }
                            }) { Text("Parolni unutdingizmi?", color = Brand, fontSize = 13.sp) }
                        }
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton(
                            text = "Kirish", isLoading = authState.isLoading,
                            enabled = email.isNotBlank() && password.isNotBlank()
                        ) {
                            viewModel.loginWithEmail(email, password,
                                onSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } },
                                onError = {})
                        }
                    }
                    AuthTab.PHONE -> PhoneSection(
                        viewModel = viewModel, activity = activity,
                        onSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            DividerOr()
            Spacer(Modifier.height(20.dp))

            // Social buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                SocialCircleButton(
                    isLoading = authState.isLoading,
                    label = "Google",
                    icon = {
                        Text("G", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                            color = Color(0xFF4285F4))
                    },
                    onClick = {
                        scope.launch {
                            performGoogleSignIn(context, activity,
                                viewModel, navController, snackbarHostState)
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Akkaunt yo'qmi? ", color = Color(0xFF8A8A9A), fontSize = 14.sp)
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text("Ro'yxatdan o'ting", color = Brand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// REGISTER SCREEN
// ════════════════════════════════════════════════════════════════════════════
@Composable
fun RegisterScreen(viewModel: AppViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreed by remember { mutableStateOf(false) }

    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState.error) {
        authState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearAuthError() }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, containerColor = AuthBg) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(AuthBg)
                .verticalScroll(rememberScrollState())
                .padding(pv)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF1A1A2E))
            }
            Spacer(Modifier.height(16.dp))
            Text("Hisob yaratish", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(4.dp))
            Text("Barcha imkoniyatlardan foydalaning", color = Color(0xFF8A8A9A), fontSize = 15.sp)
            Spacer(Modifier.height(32.dp))

            AuthField(value = name, onChange = { name = it }, label = "Ism Familiya", icon = Icons.Default.Person)
            Spacer(Modifier.height(16.dp))
            AuthField(value = email, onChange = { email = it }, label = "Email",
                icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(16.dp))
            AuthField(value = password, onChange = { password = it }, label = "Parol",
                icon = Icons.Default.Lock, keyboardType = KeyboardType.Password,
                isPassword = true, passwordVisible = passwordVisible,
                onToggle = { passwordVisible = !passwordVisible })
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agreed, onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = Brand))
                Text("Foydalanish ", color = Color(0xFF1A1A2E), fontSize = 13.sp)
                Text("shartlari", color = Brand, fontSize = 13.sp,
                    modifier = Modifier.clickable {})
                Text(" va ", color = Color(0xFF1A1A2E), fontSize = 13.sp)
                Text("maxfiylik", color = Brand, fontSize = 13.sp,
                    modifier = Modifier.clickable {})
            }
            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = "Ro'yxatdan o'tish", isLoading = authState.isLoading,
                enabled = email.isNotBlank() && password.length >= 6 && agreed
            ) {
                viewModel.registerWithEmail(name, email, password,
                    onSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } },
                    onError = {})
            }

            Spacer(Modifier.height(24.dp))
            DividerOr()
            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SocialCircleButton(isLoading = authState.isLoading, label = "Google",
                    icon = { Text("G", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4285F4)) },
                    onClick = {
                        scope.launch {
                            performGoogleSignIn(context, activity,
                                viewModel, navController, snackbarHostState)
                        }
                    })
            }

            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Akkauntingiz bormi? ", color = Color(0xFF8A8A9A), fontSize = 14.sp)
                TextButton(onClick = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } }) {
                    Text("Kirish", color = Brand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// GUEST PROMPT
// ════════════════════════════════════════════════════════════════════════════
@Composable
fun GuestPromptScreen(title: String, message: String, navController: NavController) {
    Column(Modifier.fillMaxSize().background(AuthBg)) {
        Surface(Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 2.dp) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                modifier = Modifier.padding(20.dp), color = Color(0xFF1A1A2E))
        }
        Column(Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(80.dp).clip(CircleShape).background(Brand.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(36.dp), tint = Brand)
            }
            Spacer(Modifier.height(20.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(8.dp))
            Text(message, color = Color(0xFF8A8A9A), textAlign = TextAlign.Center, fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            PrimaryButton("Kirish", isLoading = false, enabled = true) { navController.navigate(Screen.Login.route) }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Brand)) {
                Text("Ro'yxatdan o'tish", color = Brand, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

enum class AuthTab { EMAIL, PHONE }

@Composable
private fun AuthTabRow(selected: AuthTab, onSelect: (AuthTab) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8E8F0)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AuthTab.entries.forEach { tab ->
            val sel = tab == selected
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (sel) Brand else Color.Transparent)
                    .clickable { onSelect(tab) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (tab == AuthTab.EMAIL) "Email" else "Telefon",
                    color = if (sel) Color.White else Color(0xFF8A8A9A),
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AuthField(
    value: String, onChange: (String) -> Unit, label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false, passwordVisible: Boolean = false,
    onToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, color = Color(0xFF8A8A9A)) },
        leadingIcon = { Icon(icon, null, tint = Brand, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (isPassword) ({
            IconButton(onClick = { onToggle?.invoke() }) {
                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null, tint = Color(0xFF8A8A9A))
            }
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Brand,
            unfocusedBorderColor = Color(0xFFE0E0EE),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = Brand,
            cursorColor = Brand
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun PrimaryButton(text: String, isLoading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Brand, disabledContainerColor = Brand.copy(alpha = 0.5f)),
        enabled = enabled && !isLoading,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
private fun DividerOr() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE0E0EE))
        Text("  atau  ", color = Color(0xFF8A8A9A), fontSize = 13.sp)
        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE0E0EE))
    }
}

@Composable
private fun SocialCircleButton(
    isLoading: Boolean, label: String,
    icon: @Composable () -> Unit, onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(56.dp).clip(CircleShape)
                .background(Color.White)
                .border(1.5.dp, Color(0xFFE0E0EE), CircleShape)
                .clickable(enabled = !isLoading) { onClick() },
            contentAlignment = Alignment.Center
        ) { icon() }
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color(0xFF8A8A9A), fontSize = 11.sp)
    }
}

// ─── Phone OTP Section ───────────────────────────────────────────────────────
@Composable
private fun PhoneSection(viewModel: AppViewModel, activity: Activity, onSuccess: () -> Unit) {
    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    var phone by remember { mutableStateOf("+998") }
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    AnimatedContent(targetState = authState.phoneStep, label = "phoneStep") { step ->
        when (step) {
            PhoneStep.ENTER_NUMBER -> Column {
                AuthField(value = phone, onChange = { phone = it }, label = "Telefon (+998XXXXXXXXX)",
                    icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(12.dp))
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text("Tasdiqlash paytida Google xavfsizlik tekshiruvi yoki captcha oynasi chiqishi mumkin.")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Security, contentDescription = null)
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        disabledContainerColor = Brand.copy(alpha = 0.08f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconContentColor = Brand
                    )
                )
                Spacer(Modifier.height(20.dp))
                PrimaryButton("SMS kod yuborish", isLoading = authState.isLoading,
                    enabled = phone.length >= 12) {
                    viewModel.sendPhoneOtp(phone, activity, onAutoVerified = onSuccess)
                }
            }
            PhoneStep.ENTER_OTP -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Sms, null, tint = Brand, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text("SMS kodni kiriting", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A2E))
                Spacer(Modifier.height(6.dp))
                Text("$phone ga yuborildi", color = Color(0xFF8A8A9A), fontSize = 14.sp)
                Spacer(Modifier.height(32.dp))

                // 6 OTP boxes
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    otpDigits.forEachIndexed { i, digit ->
                        OutlinedTextField(
                            value = digit,
                            onValueChange = { v ->
                                val c = v.filter { it.isDigit() }.take(1)
                                otpDigits = otpDigits.toMutableList().also { it[i] = c }
                                if (c.isNotEmpty()) {
                                    if (i < 5) focusRequesters[i + 1].requestFocus()
                                    else focusManager.clearFocus()
                                }
                            },
                            modifier = Modifier.width(46.dp).height(56.dp).focusRequester(focusRequesters[i]),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand,
                                unfocusedBorderColor = Color(0xFFE0E0EE),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = Brand
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kod kelmadimi? ", color = Color(0xFF8A8A9A), fontSize = 13.sp)
                    TextButton(onClick = { viewModel.resetPhoneStep() }) {
                        Text("Qayta yuborish", color = ColorError, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                PrimaryButton("Tasdiqlash", isLoading = authState.isLoading,
                    enabled = otpDigits.all { it.isNotEmpty() }) {
                    viewModel.verifyPhoneOtp(otpDigits.joinToString(""),
                        onSuccess = onSuccess, onError = {})
                }
                TextButton(onClick = { viewModel.resetPhoneStep(); otpDigits = List(6) { "" } }) {
                    Text("← Raqamni o'zgartirish", color = Color(0xFF8A8A9A), fontSize = 13.sp)
                }
            }
        }
    }
    LaunchedEffect(authState.phoneStep) {
        if (authState.phoneStep == PhoneStep.ENTER_OTP) {
            try { focusRequesters[0].requestFocus() } catch (_: Exception) {}
        }
    }
}

// ─── Google Sign-In ──────────────────────────────────────────────────────────
private suspend fun performGoogleSignIn(
    context: android.content.Context,
    activity: Activity,
    viewModel: AppViewModel,
    navController: NavController,
    snackbar: SnackbarHostState
) {
    val webClientId = resolveGoogleWebClientId(context)
    if (webClientId == null) {
        snackbar.showSnackbar(
            "Google kirish hali sozlanmagan. Firebase Console'da Google provider ni yoqing va yangilangan google-services.json faylini yuklang."
        )
        return
    }
    val credentialManager = CredentialManager.create(context)
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()
        ).build()
    try {
        val result = credentialManager.getCredential(request = request, context = activity)
        val credential = result.credential
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            snackbar.showSnackbar("Google kirish uchun yaroqli credential olinmadi.")
            return
        }
        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        viewModel.signInWithGoogle(idToken,
            onSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } },
            onError = {}
        )
    } catch (e: GetCredentialException) {
        val msg = when {
            e.javaClass.simpleName == "NoCredentialException" ||
                e.message?.contains("No credentials available") == true ->
                "Google akkaunt topilmadi. Qurilmangizda Google akkaunt qo'shing."
            e.message?.contains("Cancel", ignoreCase = true) == true ->
                "Google kirish bekor qilindi."
            else -> "Google kirish xatosi. OAuth sozlamalari va google-services.json ni tekshiring."
        }
        snackbar.showSnackbar(msg)
    } catch (e: Exception) {
        snackbar.showSnackbar("Xatolik: ${e.message?.take(80)}")
    }
}

private fun resolveGoogleWebClientId(context: android.content.Context): String? {
    val resId = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )
    if (resId == 0) return null
    return context.getString(resId).takeUnless { it.isBlank() }
}
