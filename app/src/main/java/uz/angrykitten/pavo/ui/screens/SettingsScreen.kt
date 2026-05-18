package uz.angrykitten.pavo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import uz.angrykitten.pavo.ui.localization.AppLanguage
import uz.angrykitten.pavo.ui.localization.tr
import uz.angrykitten.pavo.ui.navigation.Screen
import uz.angrykitten.pavo.ui.theme.Brand
import uz.angrykitten.pavo.ui.theme.GradientEnd
import uz.angrykitten.pavo.ui.theme.GradientStart
import uz.angrykitten.pavo.ui.theme.LocalDarkTheme
import uz.angrykitten.pavo.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    navController: NavController,
    onToggleTheme: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.UZ,
    onChangeLanguage: (AppLanguage) -> Unit = {}
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()
    val isDark = LocalDarkTheme.current

    // Edit state
    var editingName by remember { mutableStateOf(false) }
    var editingPhone by remember { mutableStateOf(false) }
    var nameInput by remember(userName) { mutableStateOf(userName ?: "") }
    var phoneInput by remember(userPhone) { mutableStateOf(userPhone?.removePrefix("+998") ?: "") }

    // Dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete account dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    tr("Akkauntni o'chirish", "Delete account", "Удалить аккаунт"),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935)
                )
            },
            text = {
                Column {
                    Text(
                        tr(
                            "Bu amalni qaytarib bo'lmaydi. Barcha ma'lumotlaringiz o'chiriladi.",
                            "This action cannot be undone. All your data will be deleted.",
                            "Это действие нельзя отменить. Все ваши данные будут удалены."
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        tr("Haqiqatan ham akkauntni o'chirmoqchimisiz?", "Are you sure you want to delete your account?", "Вы уверены, что хотите удалить аккаунт?"),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount {
                            showDeleteDialog = false
                            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(18.dp)
                ) { Text(tr("O'chirish", "Delete", "Удалить"), fontWeight = FontWeight.Bold, color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(18.dp)
                ) { Text(tr("Bekor qilish", "Cancel", "Отмена"), color = MaterialTheme.colorScheme.onSurface) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tr("Sozlamalar", "Settings", "Настройки"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // ── Profile header card ──────────────────────────────────────
            if (isLoggedIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                (userName?.take(1) ?: "U").uppercase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(userName ?: tr("Foydalanuvchi", "User", "Пользователь"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(userEmail ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── Profile info section ──────────────────────────────
                SettingsSectionHeader(tr("Profil ma'lumotlari", "Profile details", "Данные профиля"))

                // Name edit
                SettingsItemCard {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Person, null, tint = Brand, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(tr("Ism Familiya", "Full name", "Полное имя"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userName ?: "-", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            IconButton(onClick = { editingName = !editingName }) {
                                Icon(
                                    if (editingName) Icons.Default.Close else Icons.Default.Edit,
                                    null, tint = Brand, modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        AnimatedVisibility(editingName) {
                            Column {
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text(tr("Yangi ism", "New name", "Новое имя")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand),
                                    singleLine = true
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (nameInput.isNotBlank()) {
                                            viewModel.updateUserName(nameInput)
                                            editingName = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                                ) { Text(tr("Saqlash", "Save", "Сохранить"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface) }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Phone edit
                SettingsItemCard {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Phone, null, tint = Brand, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(tr("Telefon raqam", "Phone number", "Номер телефона"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userPhone ?: tr("Kiritilmagan", "Not set", "Не указан"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            IconButton(onClick = { editingPhone = !editingPhone }) {
                                Icon(
                                    if (editingPhone) Icons.Default.Close else Icons.Default.Edit,
                                    null, tint = Brand, modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        AnimatedVisibility(editingPhone) {
                            Column {
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it.filter { c -> c.isDigit() }.take(9) },
                                    label = { Text(tr("Telefon raqam", "Phone number", "Номер телефона")) },
                                    prefix = {
                                        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                            Text("+998", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    placeholder = { Text("XX XXX XX XX") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (phoneInput.isNotBlank()) {
                                            viewModel.updateUserPhone("+998$phoneInput")
                                            editingPhone = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                                ) { Text(tr("Saqlash", "Save", "Сохранить"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface) }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            // ── Appearance section ────────────────────────────────────
            SettingsSectionHeader(tr("Ko'rinish", "Appearance", "Внешний вид"))

            SettingsItemCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                                null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(tr("Mavzu rejimi", "Theme mode", "Режим темы"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                if (isDark) tr("Qorong'u", "Dark", "Темная") else tr("Yorug'", "Light", "Светлая"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            // Dark mode ON
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Brand,
                            checkedBorderColor = Brand,
                            // Dark mode OFF (light theme)
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Info section ─────────────────────────────────────────
            SettingsItemCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, null, tint = Brand, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text(tr("Ilova tili", "App language", "Язык приложения"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text(currentLanguageLabel(currentLanguage), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(AppLanguage.UZ, AppLanguage.EN, AppLanguage.RU).forEach { language ->
                            FilterChip(
                                selected = currentLanguage == language,
                                onClick = { onChangeLanguage(language) },
                                label = { Text(languageChip(language)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Brand,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            SettingsSectionHeader(tr("Ilova", "App", "Приложение"))

            SettingsItemCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsInfoRow(Icons.Default.Info, tr("Versiya", "Version", "Версия"), "1.0.0")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsInfoRow(Icons.Default.Code, tr("Paket", "Package", "Пакет"), "uz.angrykitten.Pavo")
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Danger zone ─────────────────────────────────────────
            if (isLoggedIn) {
                SettingsSectionHeader(tr("Xavfli zona", "Danger zone", "Опасная зона"))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFE53935), modifier = Modifier.size(22.dp))
                            Column(Modifier.weight(1f)) {
                                Text(tr("Akkauntni o'chirish", "Delete account", "Удалить аккаунт"), fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                                Text(
                                    tr("Barcha ma'lumotlar butunlay o'chiriladi", "All account data will be permanently removed", "Все данные аккаунта будут удалены безвозвратно"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                        ) {
                            Icon(Icons.Default.Warning, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(tr("Akkauntni o'chirish", "Delete account", "Удалить аккаунт"), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.1.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsItemCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) { content() }
}

@Composable
private fun SettingsInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Brand, modifier = Modifier.size(18.dp))
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        Text(value, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun currentLanguageLabel(language: AppLanguage): String {
    return when (language) {
        AppLanguage.UZ -> tr("O'zbek tili", "Uzbek", "Узбекский")
        AppLanguage.EN -> tr("Ingliz tili", "English", "Английский")
        AppLanguage.RU -> tr("Rus tili", "Russian", "Русский")
    }
}

private fun languageChip(language: AppLanguage): String {
    return when (language) {
        AppLanguage.UZ -> "UZ"
        AppLanguage.EN -> "EN"
        AppLanguage.RU -> "RU"
    }
}
