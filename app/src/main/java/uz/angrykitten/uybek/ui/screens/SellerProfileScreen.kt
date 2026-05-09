package uz.angrykitten.uybek.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import uz.angrykitten.uybek.ui.components.ListingLayoutToggle
import uz.angrykitten.uybek.ui.components.PropertyCard
import uz.angrykitten.uybek.ui.components.PropertyGridCard
import uz.angrykitten.uybek.ui.localization.tr
import uz.angrykitten.uybek.ui.navigation.Screen
import uz.angrykitten.uybek.ui.theme.AccentGold
import uz.angrykitten.uybek.ui.theme.AccentSky
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.theme.BrandDark
import uz.angrykitten.uybek.ui.theme.BrandLight
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun SellerProfileScreen(
    sellerId: String,
    viewModel: AppViewModel,
    navController: NavController
) {
    val allProperties = viewModel.getAllProperties()
    val sellerProperties = remember(sellerId, allProperties) {
        allProperties.filter { it.user_id == sellerId }
    }
    val seller = sellerProperties.firstOrNull()

    val sellerName = seller?.seller_name ?: "Foydalanuvchi"
    val sellerPhone = seller?.seller_phone.orEmpty()
    val sellerTelegram = seller?.seller_whatsapp.orEmpty()

    val currentUserId by viewModel.userId.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()

    var columns by rememberSaveable { mutableIntStateOf(1) }
    val layoutColumns = if (columns == 1) 1 else 2

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tr("Sotuvchi profili", "Seller profile", "Профиль продавца"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Orqaga", "Back", "Назад"))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(layoutColumns),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            fullWidthItem {
                SellerHeroCard(
                    sellerName = sellerName,
                    sellerPhone = sellerPhone,
                    sellerTelegram = sellerTelegram,
                    listingCount = sellerProperties.size,
                    canMessage = isLoggedIn && currentUserId != null && currentUserId != sellerId && sellerId.isNotBlank(),
                    onOpenChat = {
                        val chatId = listOf(currentUserId!!, sellerId).sorted().joinToString("_")
                        navController.navigate(Screen.ChatDetail.createRoute(chatId, sellerId, sellerName))
                    }
                )
            }

            fullWidthItem {
                Card(
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = tr("Sotuvchining e'lonlari", "Seller listings", "Объявления продавца"),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = tr("${sellerProperties.size} ta faol mulk", "${sellerProperties.size} active properties", "${sellerProperties.size} активных объектов"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        ListingLayoutToggle(columns = layoutColumns, onColumnsChange = { columns = it })
                    }
                }
            }

            if (sellerProperties.isEmpty()) {
                fullWidthItem {
                    EmptySellerListings()
                }
            } else {
                items(sellerProperties, key = { it.id }) { property ->
                    if (layoutColumns == 1) {
                        PropertyCard(
                            property = property,
                            isSaved = property.id in savedIds,
                            onCardClick = { navController.navigate(Screen.PropertyDetail.createRoute(property.id)) },
                            onToggleSave = {
                                if (isLoggedIn) viewModel.toggleSaved(property.id)
                                else navController.navigate(Screen.Login.route)
                            }
                        )
                    } else {
                        PropertyGridCard(
                            property = property,
                            isSaved = property.id in savedIds,
                            onCardClick = { navController.navigate(Screen.PropertyDetail.createRoute(property.id)) },
                            onToggleSave = {
                                if (isLoggedIn) viewModel.toggleSaved(property.id)
                                else navController.navigate(Screen.Login.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerHeroCard(
    sellerName: String,
    sellerPhone: String,
    sellerTelegram: String,
    listingCount: Int,
    canMessage: Boolean,
    onOpenChat: () -> Unit
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sellerName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = sellerName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$listingCount ta faol e'lon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SellerInfoPill(
                        icon = Icons.Default.Home,
                        text = "$listingCount ta e'lon",
                        modifier = Modifier.weight(1f)
                    )
                    if (sellerPhone.isNotBlank()) {
                        SellerInfoPill(
                            icon = Icons.Default.Call,
                            text = sellerPhone,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (sellerTelegram.isNotBlank()) {
                    SellerInfoPill(
                        icon = Icons.Default.Send,
                        text = sellerTelegram,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (canMessage) {
                    Button(
                        onClick = onOpenChat,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Brand
                        )
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(tr("Ilova orqali yozish", "Message in app", "Написать в приложении"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerInfoPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AccentGold)
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptySellerListings() {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(BrandLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Brand, modifier = Modifier.size(34.dp))
            }
            Text(tr("Hozircha e'lonlar topilmadi", "No listings yet", "Объявлений пока нет"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = tr("Bu foydalanuvchi uchun ko'rsatiladigan faol mulklar yo'q.", "There are no active properties to show for this user.", "Для этого пользователя нет активных объектов."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun LazyGridScope.fullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        content()
    }
}
