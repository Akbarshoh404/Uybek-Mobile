package uz.angrykitten.uybek.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.angrykitten.uybek.ui.theme.AccentSky
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.theme.BrandDark
import uz.angrykitten.uybek.ui.theme.BrandLight

private val PageCardShape = RoundedCornerShape(30.dp)
private val InlineCardShape = RoundedCornerShape(24.dp)

private data class FaqItem(val question: String, val answer: String)
private data class PolicyItem(val title: String, val body: String)

private val faqItems = listOf(
    FaqItem(
        "Uybek nima?",
        "Uybek O'zbekistondagi ko'chmas mulk bozori uchun mobil ilova. U orqali uy, kvartira, tijorat va yer e'lonlarini topish, saqlash va joylashtirish mumkin."
    ),
    FaqItem(
        "E'lon berish uchun nimalar kerak?",
        "Avval akkauntga kiring yoki ro'yxatdan o'ting. Keyin pastdagi e'lon berish bo'limidan mulk tafsilotlarini bosqichma-bosqich to'ldiring."
    ),
    FaqItem(
        "E'lon berish pullikmi?",
        "Hozirgi versiyada e'lon joylashtirish bepul. Keyingi versiyalarda premium ko'tarish yoki reklama xizmatlari qo'shilishi mumkin."
    ),
    FaqItem(
        "Sotuvchi bilan qanday bog'lanaman?",
        "E'lon tafsiloti sahifasida qo'ng'iroq, Telegram va ilova ichidagi chat tugmalari bor."
    ),
    FaqItem(
        "Saqlangan e'lonlarni qayerdan topaman?",
        "Pastki navigatsiyadagi Saqlangan bo'limida yurak bilan belgilangan barcha e'lonlar ko'rsatiladi."
    ),
    FaqItem(
        "Akkauntimni qanday o'chiraman?",
        "Profil ichidagi Sozlamalar sahifasidan xavfli zona bo'limiga o'tib akkauntni o'chirishingiz mumkin."
    ),
    FaqItem(
        "Qaysi tillar mavjud?",
        "Hozirgi interfeys asosan o'zbek tilida. To'liq ko'p tillilik keyingi ishlab chiqish bosqichlariga kiradi."
    ),
    FaqItem(
        "Xatolik yoki taklifni qayerga yuboraman?",
        "Support kanaliga yoki kelajakdagi yordam markazi shakliga yuborish tavsiya etiladi. Hozircha loyiha ichida maxsus feedback oqimi yo'q."
    )
)

private val policyItems = listOf(
    PolicyItem(
        "1. Qanday ma'lumotlar yig'iladi",
        "Hisob ma'lumotlari, telefon raqami, e'lon matnlari, mulk tafsilotlari va ilova ichidagi faoliyat uchun zarur bo'lgan texnik ma'lumotlar yig'ilishi mumkin."
    ),
    PolicyItem(
        "2. Ma'lumotlardan foydalanish",
        "Bu ma'lumotlar akkaunt yaratish, e'lonlarni chiqarish, chatni yuritish, foydalanuvchi tajribasini yaxshilash va xavfsizlikni ta'minlash uchun ishlatiladi."
    ),
    PolicyItem(
        "3. Saqlash va xavfsizlik",
        "Loyihada Firebase va Supabase servislaridan foydalaniladi. Ishlab chiqarish versiyasida zaxiralash, audit va qat'iy ruxsat siyosatlari bilan bu qatlam mustahkamlanishi kerak."
    ),
    PolicyItem(
        "4. Uchinchi tomon servislar",
        "Autentifikatsiya, realtime chat va ma'lumotlar bazasi uchun uchinchi tomon servislar ishlatiladi. Har bir servis o'zining maxfiylik va xavfsizlik qoidalariga ega."
    ),
    PolicyItem(
        "5. Foydalanuvchi huquqlari",
        "Foydalanuvchi o'z profilini yangilashi, saqlangan ma'lumotlarni ko'rishi va akkauntni o'chirishni so'rashi mumkin."
    ),
    PolicyItem(
        "6. Kelajakdagi talablar",
        "To'liq versiyada aniq maxfiylik siyosati URL manzili, kontakt ma'lumotlari, rozilik boshqaruvi, analytics opt-in va moderatsiya siyosatlari qo'shilishi kerak."
    )
)

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun FAQScreen(navController: NavController) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FAQ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                InfoHeaderCard(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = "Ko'p so'raladigan savollar",
                    subtitle = "Ilova, e'lonlar va akkaunt bo'yicha tez javoblar"
                )
            }

            itemsIndexed(faqItems) { index, item ->
                FaqCard(
                    item = item,
                    expanded = expandedIndex == index,
                    onToggle = { expandedIndex = if (expandedIndex == index) null else index }
                )
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maxfiylik siyosati", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InfoHeaderCard(
                icon = Icons.Default.Shield,
                title = "Ma'lumotlaringiz qanday ishlatiladi",
                subtitle = "Amaldagi loyiha holati uchun qisqa va tushunarli siyosat"
            )

            policyItems.forEach { item ->
                PolicySection(item = item)
            }

            Surface(
                shape = InlineCardShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Text(
                    text = "Eslatma: bu sahifa UI ichida mavjud, lekin ishlab chiqarish versiyasi uchun yuridik jihatdan to'liq va tashqi URL bilan boshqariladigan siyosat tayyorlanishi kerak.",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun InfoHeaderCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        shape = PageCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(BrandDark, Brand, AccentSky.copy(alpha = 0.78f))
                    )
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
private fun FaqCard(
    item: FaqItem,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "faq_arrow")

    Card(
        shape = InlineCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Q", color = Brand, fontWeight = FontWeight.Black)
                }

                Text(
                    text = item.question,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Black)
                        }

                        Text(
                            text = item.answer,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicySection(item: PolicyItem) {
    Card(
        shape = InlineCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Brand)
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = item.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}
