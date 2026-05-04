package uz.angrykitten.uybek.ui.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import uz.angrykitten.uybek.BuildConfig
import uz.angrykitten.uybek.ui.navigation.Screen
import uz.angrykitten.uybek.ui.theme.*
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel
import uz.angrykitten.uybek.ui.viewmodel.PhoneStep

// Auth colors now come from MaterialTheme — supports light & dark mode
private val AuthBg @Composable get() = MaterialTheme.colorScheme.background
private val AuthSurface @Composable get() = MaterialTheme.colorScheme.surface
private val AuthBorder @Composable get() = MaterialTheme.colorScheme.outline
private val AuthText @Composable get() = MaterialTheme.colorScheme.onBackground
private val AuthSubtext @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

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
        containerColor = MaterialTheme.colorScheme.background
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(pv)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo
            Box(
                Modifier.size(56.dp).background(Brand),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(24.dp))

            Text("Xush kelibsiz!", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = AuthText)
            Spacer(Modifier.height(4.dp))
            Text("Email yoki telefon bilan kiring", color = AuthSubtext, fontSize = 15.sp)
            Spacer(Modifier.height(32.dp))

            AuthTabRow(selected = activeTab, onSelect = { activeTab = it; viewModel.resetPhoneStep() })
            Spacer(Modifier.height(24.dp))

            AnimatedContent(targetState = activeTab, label = "tab") { tab ->
                when (tab) {
                    AuthTab.EMAIL -> Column {
                        DarkAuthField(value = email, onChange = { email = it }, label = "Email",
                            icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
                        Spacer(Modifier.height(16.dp))
                        DarkAuthField(value = password, onChange = { password = it }, label = "Parol",
                            icon = Icons.Default.Lock, keyboardType = KeyboardType.Password,
                            isPassword = true, passwordVisible = passwordVisible,
                            onToggle = { passwordVisible = !passwordVisible })
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                if (email.isNotBlank()) viewModel.sendPasswordReset(email) {
                                    scope.launch { snackbarHostState.showSnackbar(if (it) "Tiklash xati yuborildi ✓" else "Email topilmadi") }
                                }
                            }) { Text("Parolni unutdingizmi?", color = Brand, fontSize = 13.sp) }
                        }
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton(text = "Kirish", isLoading = authState.isLoading,
                            enabled = email.isNotBlank() && password.isNotBlank()) {
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
            DarkDividerOr()
            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                SocialCircleButton(
                    isLoading = authState.isLoading, label = "Google",
                    icon = { Text("G", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4285F4)) },
                    onClick = {
                        scope.launch { performGoogleSignIn(context, activity, viewModel, navController, snackbarHostState) }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Guest option ──
            TextButton(
                onClick = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mehmon sifatida davom etish", color = AuthSubtext, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, null, tint = AuthSubtext, modifier = Modifier.size(16.dp))
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Akkaunt yo'qmi? ", color = AuthSubtext, fontSize = 14.sp)
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

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, containerColor = MaterialTheme.colorScheme.background) { pv ->
        Column(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())
                .padding(pv).padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AuthText)
            }
            Spacer(Modifier.height(16.dp))
            Text("Hisob yaratish", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = AuthText)
            Spacer(Modifier.height(4.dp))
            Text("Barcha imkoniyatlardan foydalaning", color = AuthSubtext, fontSize = 15.sp)
            Spacer(Modifier.height(32.dp))

            DarkAuthField(value = name, onChange = { name = it }, label = "Ism Familiya", icon = Icons.Default.Person)
            Spacer(Modifier.height(16.dp))
            DarkAuthField(value = email, onChange = { email = it }, label = "Email",
                icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(16.dp))
            DarkAuthField(value = password, onChange = { password = it }, label = "Parol",
                icon = Icons.Default.Lock, keyboardType = KeyboardType.Password,
                isPassword = true, passwordVisible = passwordVisible,
                onToggle = { passwordVisible = !passwordVisible })
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agreed, onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = Brand, uncheckedColor = AuthBorder))
                Text("Foydalanish ", color = AuthText, fontSize = 13.sp)
                Text("shartlari", color = Brand, fontSize = 13.sp, modifier = Modifier.clickable {})
                Text(" va ", color = AuthText, fontSize = 13.sp)
                Text("maxfiylik", color = Brand, fontSize = 13.sp, modifier = Modifier.clickable {})
            }
            Spacer(Modifier.height(24.dp))

            PrimaryButton(text = "Ro'yxatdan o'tish", isLoading = authState.isLoading,
                enabled = email.isNotBlank() && password.length >= 6 && agreed) {
                viewModel.registerWithEmail(name, email, password,
                    onSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } },
                    onError = {})
            }

            Spacer(Modifier.height(24.dp))
            DarkDividerOr()
            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SocialCircleButton(isLoading = authState.isLoading, label = "Google",
                    icon = { Text("G", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4285F4)) },
                    onClick = {
                        scope.launch { performGoogleSignIn(context, activity, viewModel, navController, snackbarHostState) }
                    })
            }

            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Akkauntingiz bormi? ", color = AuthSubtext, fontSize = 14.sp)
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Kirish", color = Brand, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// GUEST PROMPT (soft, non-blocking)
// ════════════════════════════════════════════════════════════════════════════
@Composable
fun GuestPromptScreen(title: String, message: String, navController: NavController) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 0.dp) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                modifier = Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurface)
        }
        Column(Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(80.dp).background(Brand.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(36.dp), tint = Brand)
            }
            Spacer(Modifier.height(20.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))
            PrimaryButton("Kirish", isLoading = false, enabled = true) { navController.navigate(Screen.Login.route) }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                Text("Ro'yxatdan o'tish", fontWeight = FontWeight.SemiBold, color = AuthText)
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

enum class AuthTab { EMAIL, PHONE }

@Composable
private fun AuthTabRow(selected: AuthTab, onSelect: (AuthTab) -> Unit) {
    Row(
        Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
            .background(MaterialTheme.colorScheme.surface).padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        AuthTab.entries.forEach { tab ->
            val sel = tab == selected
            Box(
                Modifier.weight(1f)
                    .background(if (sel) Brand else Color.Transparent)
                    .clickable { onSelect(tab) }.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (tab == AuthTab.EMAIL) "Email" else "Telefon",
                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
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
    DarkAuthField(value, onChange, label, icon, keyboardType, isPassword, passwordVisible, onToggle)
}

@Composable
fun DarkAuthField(
    value: String, onChange: (String) -> Unit, label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false, passwordVisible: Boolean = false,
    onToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = Brand, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (isPassword) ({
            IconButton(onClick = { onToggle?.invoke() }) {
                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Brand,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = Brand
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
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Brand,
            disabledContainerColor = Brand.copy(alpha = 0.3f)
        ),
        enabled = enabled && !isLoading,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
private fun DarkDividerOr() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        Text("  yoki  ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun SocialCircleButton(
    isLoading: Boolean, label: String,
    icon: @Composable () -> Unit, onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(56.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(0.dp))
                .clickable(enabled = !isLoading) { onClick() },
            contentAlignment = Alignment.Center
        ) { icon() }
        Spacer(Modifier.height(4.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
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
                DarkAuthField(value = phone, onChange = { phone = it }, label = "Telefon (+998XXXXXXXXX)",
                    icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(20.dp))
                PrimaryButton("SMS kod yuborish", isLoading = authState.isLoading, enabled = phone.length >= 12) {
                    viewModel.sendPhoneOtp(phone, activity, onAutoVerified = onSuccess)
                }
            }
            PhoneStep.ENTER_OTP -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Sms, null, tint = Brand, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text("SMS kodni kiriting", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AuthText)
                Spacer(Modifier.height(6.dp))
                Text("$phone ga yuborildi", color = AuthSubtext, fontSize = 14.sp)
                Spacer(Modifier.height(32.dp))
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
                            shape = RoundedCornerShape(0.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand, unfocusedBorderColor = AuthBorder,
                                focusedContainerColor = AuthSurface, unfocusedContainerColor = AuthSurface,
                                focusedTextColor = AuthText, unfocusedTextColor = AuthText, cursorColor = Brand
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AuthText
                            ),
                            singleLine = true
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kod kelmadimi? ", color = AuthSubtext, fontSize = 13.sp)
                    TextButton(onClick = { viewModel.resetPhoneStep() }) {
                        Text("Qayta yuborish", color = ColorError, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                PrimaryButton("Tasdiqlash", isLoading = authState.isLoading,
                    enabled = otpDigits.all { it.isNotEmpty() }) {
                    viewModel.verifyPhoneOtp(otpDigits.joinToString(""), onSuccess = onSuccess, onError = {})
                }
                TextButton(onClick = { viewModel.resetPhoneStep(); otpDigits = List(6) { "" } }) {
                    Text("← Raqamni o'zgartirish", color = AuthSubtext, fontSize = 13.sp)
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
        snackbar.showSnackbar("Google kirish: Firebase Console'dan Web Client ID ni local.properties ga GOOGLE_WEB_CLIENT_ID=... qilib qo'shing.")
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
            else -> "Google kirish xatosi: ${e.message?.take(60)}"
        }
        snackbar.showSnackbar(msg)
    } catch (e: Exception) {
        snackbar.showSnackbar("Xatolik: ${e.message?.take(80)}")
    }
}

private fun resolveGoogleWebClientId(context: android.content.Context): String? {
    // 1. Check build config (set via local.properties GOOGLE_WEB_CLIENT_ID)
    BuildConfig.GOOGLE_WEB_CLIENT_ID.takeUnless { it.isBlank() }?.let { return it }
    // 2. Check auto-generated string from google-services.json (only present when oauth_client is configured)
    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    if (resId != 0) {
        val id = context.getString(resId)
        if (id.isNotBlank() && !id.startsWith("YOUR_")) return id
    }
    return null
}
