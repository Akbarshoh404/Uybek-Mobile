package uz.angrykitten.uybek.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.theme.GradientEnd
import uz.angrykitten.uybek.ui.theme.GradientStart

// ════════════════════════════════════════════════════════════════════════════
// FAQ SCREEN
// ════════════════════════════════════════════════════════════════════════════
private data class FaqItem(val question: String, val answer: String)

private val faqItems = listOf(
    FaqItem("Uybek nima?",
        "Uybek — O'zbekistondagi ko'chmas mulk bozori uchun mobil ilova. Kvartiralar, uylar, tijorat va yer maydonlarini sotish va ijaraga berish uchun platforma."),
    FaqItem("E'lon berish uchun nimalar kerak?",
        "E'lon berish uchun avval ilovaga ro'yxatdan o'ting yoki kiring. Keyin '+' tugmasini bosib, barcha ma'lumotlarni to'ldiring."),
    FaqItem("E'lon berish pullikmi?",
        "Hozirda e'lon berish mutlaqo bepul! Biz ko'chmas mulk bozorini hammaga ochiq qilishni maqsad qilib qo'yganmiz."),
    FaqItem("Rasmlarni qanday yuklash mumkin?",
        "E'lon yaratish jarayonida rasm qo'shish bo'limida galereyngizdan rasmlarni tanlashingiz mumkin."),
    FaqItem("Sotuvchi bilan qanday bog'lanaman?",
        "E'lon sahifasida 'Qo'ng'iroq' va 'Telegram' tugmalari mavjud. Bundan tashqari, 'Chat' bo'limidan to'g'ridan-to'g'ri yozishingiz mumkin."),
    FaqItem("Akkauntimni qanday o'chiraman?",
        "Profil > Sozlamalar > 'Akkauntni o'chirish' bo'limiga o'ting. E'tibor bering: bu amalni qaytarib bo'lmaydi."),
    FaqItem("Saqlangan e'lonlarni qayerdan topaman?",
        "Quyi navigatsiya satrida 'Saqlangan' tugmasini bosing. Yurakchasini bosgan barcha e'lonlar shu yerda saqlanadi."),
    FaqItem("Ilovada qanday tillar mavjud?",
        "Hozirda ilova O'zbek tilida ishlaydi. Yaqin kelajakda Rus va Ingliz tili qo'shiladi."),
    FaqItem("Joylashuv nima uchun kerak?",
        "Shahar va tuman ma'lumotlari xaridorlarga qidiruvni osonlashtiradi. Aniqroq manzil qo'shsangiz, e'loningiz ko'proq ko'riladi."),
    FaqItem("Xatolikni qanday bildiraman?",
        "Profil sahifasida 'Yordam markazi' bo'limiga o'ting yoki support@uybek.uz manziliga xat yuboring.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ko'p so'raladigan savollar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pv ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pv),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.HelpOutline, null, tint = Brand, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("FAQ", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Eng ko'p so'raladigan savollar", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            items(faqItems.size) { index ->
                val item = faqItems[index]
                val isExpanded = expandedIndex == index
                val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "chevron")

                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedIndex = if (isExpanded) null else index }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(0.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Q", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface, fontSize = 14.sp)
                            }
                            Text(
                                item.question,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                Icons.Default.ExpandMore, null,
                                tint = Brand,
                                modifier = Modifier.size(20.dp).rotate(rotation)
                            )
                        }
                        AnimatedVisibility(isExpanded) {
                            Column {
                                HorizontalDivider(color = Brand.copy(alpha = 0.1f))
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(0.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("A", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                                    }
                                    Text(
                                        item.answer,
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

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PRIVACY POLICY SCREEN
// ════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maxfiylik siyosati", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Shield, null, tint = Brand, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Maxfiylik siyosati", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Oxirgi yangilanish: 2025-yil, May", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            PolicySection("1. Ma'lumotlar to'plash",
                "Uybek ilovasi foydalanuvchilardan quyidagi ma'lumotlarni to'playdi:\n• Ism va familiya\n• Email manzil\n• Telefon raqami\n• Ko'chmas mulk e'lonlari ma'lumotlari\n• Qurilma ma'lumotlari (tizim versiyasi, qurilma identifikatori)")

            PolicySection("2. Ma'lumotlardan foydalanish",
                "Yig'ilgan ma'lumotlar quyidagi maqsadlarda ishlatiladi:\n• Ilova funksiyalarini ta'minlash\n• Foydalanuvchi hisobini boshqarish\n• E'lonlarni ko'rsatish va filtrlash\n• Xavfsizlikni ta'minlash\n• Ilovani yaxshilash")

            PolicySection("3. Ma'lumotlar saqlash",
                "Ma'lumotlaringiz xavfsiz Firebase va Supabase serverlarida saqlanadi. Biz sanoat standartidagi shifrlash usullaridan foydalanamiz. Ma'lumotlar uchinchi shaxslarga sotilmaydi.")

            PolicySection("4. Cookie va kuzatuv",
                "Ilova foydalanish statistikasini yaxshilash uchun anonim analitika ma'lumotlarini to'plashi mumkin. Shaxsiy ma'lumotlar kuzatilmaydi.")

            PolicySection("5. Uchinchi tomon xizmatlar",
                "Ilova quyidagi xizmatlardan foydalanadi:\n• Firebase Authentication — kirish\n• Firebase Realtime Database — chat\n• Supabase — ma'lumotlar bazasi\n• Google Play Services\nHar bir xizmat o'z maxfiylik siyosatiga ega.")

            PolicySection("6. Foydalanuvchi huquqlari",
                "Siz quyidagi huquqlarga egasiz:\n• Ma'lumotlaringizni ko'rish\n• Ma'lumotlaringizni o'chirish\n• Akkauntingizni o'chirish\n• Ma'lumot to'plashga rozilikni qaytarib olish\n\nAkkauntni o'chirish: Profil > Sozlamalar > Akkauntni o'chirish.")

            PolicySection("7. Bog'lanish",
                "Maxfiylik siyosati bo'yicha savollar uchun:\n📧 support@uybek.uz\n📞 +998 71 000 00 00")

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Brand, RoundedCornerShape(0.dp))
                )
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Brand)
            }
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
        }
    }
}
