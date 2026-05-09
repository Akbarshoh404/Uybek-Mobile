package uz.angrykitten.uybek.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uz.angrykitten.uybek.ui.localization.AppLanguage
import uz.angrykitten.uybek.ui.localization.tr
import uz.angrykitten.uybek.ui.navigation.Screen
import uz.angrykitten.uybek.ui.theme.AccentGold
import uz.angrykitten.uybek.ui.theme.AccentRose
import uz.angrykitten.uybek.ui.theme.AccentSky
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.theme.BrandDark
import uz.angrykitten.uybek.ui.theme.BrandLight
import uz.angrykitten.uybek.ui.theme.LocalDarkTheme
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    navController: NavController,
    onToggleTheme: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.UZ,
    onChangeLanguage: (AppLanguage) -> Unit = {}
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userAvatar by viewModel.userAvatar.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val isDark = LocalDarkTheme.current

    var showSignOutDialog by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Akkauntdan chiqish", fontWeight = FontWeight.Bold) },
            text = { Text(tr("Haqiqatan ham akkauntdan chiqmoqchimisiz?", "Do you want to sign out?", "Вы хотите выйти из аккаунта?")) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signOut()
                        showSignOutDialog = false
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDark)
                ) {
                    Text(tr("Chiqish", "Sign out", "Выйти"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutDialog = false },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(tr("Bekor qilish", "Cancel", "Отмена"))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tr("Profil", "Profile", "Профиль"), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Mavzuni almashtirish"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (isLoggedIn) {
                    LoggedInProfileHero(
                        userName = userName ?: tr("Foydalanuvchi", "User", "Пользователь"),
                        userEmail = userEmail.orEmpty(),
                        userAvatar = userAvatar,
                        listingCount = viewModel.getUserProperties().size,
                        savedCount = savedIds.size
                    )
                } else {
                    GuestProfileHero(
                        onLogin = { navController.navigate(Screen.Login.route) },
                        onRegister = { navController.navigate(Screen.Register.route) }
                    )
                }
            }

            if (isLoggedIn) {
                item { SectionLabel(tr("Mening hisobim", "My account", "Мой аккаунт")) }
                item {
                    ActionGroupCard {
                        ProfileActionRow(
                            icon = Icons.Default.List,
                            title = tr("Mening e'lonlarim", "My listings", "Мои объявления"),
                            subtitle = tr("Joylashtirilgan mulklarni boshqarish", "Manage your posted properties", "Управляйте своими объявлениями"),
                            onClick = { navController.navigate(Screen.MyListings.route) }
                        )
                        ProfileActionRow(
                            icon = Icons.Default.Favorite,
                            title = tr("Saqlangan e'lonlar", "Saved listings", "Избранные объявления"),
                            subtitle = tr("Yoqtirgan mulklaringiz ro'yxati", "Properties you liked", "Список понравившихся объектов"),
                            onClick = { navController.navigate(Screen.Saved.route) }
                        )
                    }
                }
            }

            item { SectionLabel(tr("Ilova", "App", "Приложение")) }
            item {
                ActionGroupCard {
                    ProfileActionRow(
                        icon = Icons.Default.Settings,
                        title = tr("Til", "Language", "Язык"),
                        subtitle = languageSummary(currentLanguage),
                        badge = languageBadge(currentLanguage),
                        onClick = {
                            onChangeLanguage(
                                when (currentLanguage) {
                                    AppLanguage.UZ -> AppLanguage.EN
                                    AppLanguage.EN -> AppLanguage.RU
                                    AppLanguage.RU -> AppLanguage.UZ
                                }
                            )
                        }
                    )
                    ProfileActionRow(
                        icon = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        title = if (isDark) tr("Yorug' rejim", "Light mode", "Светлая тема") else tr("Qorong'u rejim", "Dark mode", "Темная тема"),
                        subtitle = tr("Ilova ko'rinishini almashtirish", "Change the app appearance", "Сменить оформление приложения"),
                        badge = if (isDark) "Dark" else "Light",
                        onClick = onToggleTheme
                    )
                    ProfileActionRow(
                        icon = Icons.Default.Settings,
                        title = tr("Sozlamalar", "Settings", "Настройки"),
                        subtitle = tr("Profil va ilova sozlamalari", "Profile and app preferences", "Настройки профиля и приложения"),
                        onClick = { navController.navigate(Screen.Settings.route) }
                    )
                }
            }

            item { SectionLabel(tr("Ma'lumot", "Info", "Информация")) }
            item {
                ActionGroupCard {
                    ProfileActionRow(
                        icon = Icons.Default.Info,
                        title = tr("Ilova haqida", "About app", "О приложении"),
                        subtitle = tr("Uybek, versiya 1.0", "Uybek, version 1.0", "Uybek, версия 1.0"),
                        onClick = {}
                    )
                    ProfileActionRow(
                        icon = Icons.Default.Help,
                        title = tr("Yordam markazi", "Help center", "Центр помощи"),
                        subtitle = tr("Ko'p so'raladigan savollar", "Frequently asked questions", "Часто задаваемые вопросы"),
                        onClick = { navController.navigate(Screen.FAQ.route) }
                    )
                    ProfileActionRow(
                        icon = Icons.Outlined.Shield,
                        title = tr("Maxfiylik siyosati", "Privacy policy", "Политика конфиденциальности"),
                        subtitle = tr("Ma'lumotlar xavfsizligi va qoidalar", "Data safety and rules", "Безопасность данных и правила"),
                        onClick = { navController.navigate(Screen.PrivacyPolicy.route) }
                    )
                }
            }

            if (isLoggedIn) {
                item {
                    Button(
                        onClick = { showSignOutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = AccentRose)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            tr("Akkauntdan chiqish", "Sign out", "Выйти из аккаунта"),
                            color = AccentRose,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoggedInProfileHero(
    userName: String,
    userEmail: String,
    userAvatar: String?,
    listingCount: Int,
    savedCount: Int
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(BrandDark, Brand, AccentSky.copy(alpha = 0.82f))
                    )
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!userAvatar.isNullOrBlank()) {
                                AsyncImage(
                                    model = userAvatar,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = initial(userName),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.78f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricChip(label = "E'lonlar", value = listingCount.toString(), tint = AccentGold)
                    MetricChip(label = "Saqlangan", value = savedCount.toString(), tint = BrandLight)
                    MetricChip(label = "Status", value = "Faol", tint = AccentSky)
                }
            }
        }
    }
}

@Composable
private fun GuestProfileHero(onLogin: () -> Unit, onRegister: () -> Unit) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(BrandLight, AccentRose.copy(alpha = 0.25f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.size(36.dp)
                )
            }
            Text(tr("Profilingizni yarating", "Create your profile", "Создайте профиль"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = tr("Saqlanganlar, chat va e'lon boshqaruvi uchun akkauntga kiring.", "Sign in for saved listings, chat, and listing management.", "Войдите, чтобы пользоваться избранным, чатами и управлением объявлениями."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brand)
            ) {
                Text(tr("Kirish", "Sign in", "Войти"), color = Color.White, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(tr("Ro'yxatdan o'tish", "Register", "Регистрация"), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.MetricChip(
    label: String,
    value: String,
    tint: Color
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.14f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.72f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.Black, color = tint)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun ActionGroupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BrandLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Brand, modifier = Modifier.size(22.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (badge != null) {
            Surface(
                shape = CircleShape,
                color = BrandLight
            ) {
                Text(
                    text = badge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Brand,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun languageSummary(language: AppLanguage): String {
    return when (language) {
        AppLanguage.UZ -> tr("O'zbek tili faol", "Uzbek active", "Узбекский активен")
        AppLanguage.EN -> tr("Ingliz tili faol", "English active", "Английский активен")
        AppLanguage.RU -> tr("Rus tili faol", "Russian active", "Русский активен")
    }
}

private fun languageBadge(language: AppLanguage): String {
    return when (language) {
        AppLanguage.UZ -> "UZ"
        AppLanguage.EN -> "EN"
        AppLanguage.RU -> "RU"
    }
}

private fun initial(name: String): String {
    val first = name.firstOrNull()?.uppercaseChar() ?: 'U'
    return first.toString()
}
