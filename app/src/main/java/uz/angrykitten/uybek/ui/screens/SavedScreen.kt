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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import uz.angrykitten.uybek.ui.components.ListingLayoutToggle
import uz.angrykitten.uybek.ui.components.PropertyCard
import uz.angrykitten.uybek.ui.components.PropertyGridCard
import uz.angrykitten.uybek.ui.localization.tr
import uz.angrykitten.uybek.ui.navigation.Screen
import uz.angrykitten.uybek.ui.theme.AccentRose
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.theme.BrandLight
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel

@Composable
fun SavedScreen(viewModel: AppViewModel, navController: NavController) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val savedProperties by viewModel.savedProperties.collectAsStateWithLifecycle()
    var columns by rememberSaveable { mutableIntStateOf(2) }
    val layoutColumns = if (columns == 1) 1 else 2

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (!isLoggedIn) {
            SavedGuestState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onLogin = { navController.navigate(Screen.Login.route) },
                onRegister = { navController.navigate(Screen.Register.route) }
            )
            return@Scaffold
        }

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
                SavedHeader(
                    count = savedProperties.size,
                    columns = layoutColumns,
                    onColumnsChange = { columns = it }
                )
            }

            if (savedProperties.isEmpty()) {
                fullWidthItem {
                    EmptySavedState(onBrowse = { navController.navigate(Screen.Home.route) })
                }
            } else {
                items(savedProperties, key = { it.id }) { property ->
                    if (layoutColumns == 1) {
                        PropertyCard(
                            property = property,
                            isSaved = property.id in savedIds,
                            onCardClick = { navController.navigate(Screen.PropertyDetail.createRoute(property.id)) },
                            onToggleSave = { viewModel.toggleSaved(property.id) }
                        )
                    } else {
                        PropertyGridCard(
                            property = property,
                            isSaved = property.id in savedIds,
                            onCardClick = { navController.navigate(Screen.PropertyDetail.createRoute(property.id)) },
                            onToggleSave = { viewModel.toggleSaved(property.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedHeader(count: Int, columns: Int, onColumnsChange: (Int) -> Unit) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = tr("Saqlangan e'lonlar", "Saved listings", "Избранные объявления"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tr("Yoqtirgan mulklaringiz bir joyda jamlandi.", "All your liked properties are here.", "Все понравившиеся объекты собраны здесь."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    color = BrandLight
                ) {
                    Text(
                        text = tr("$count ta", "$count", "$count"),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Brand,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("Ko'rinish", "View", "Вид"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ListingLayoutToggle(columns = columns, onColumnsChange = onColumnsChange)
            }
        }
    }
}

@Composable
private fun SavedGuestState(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
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
                        .size(88.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(BrandLight, AccentRose.copy(alpha = 0.28f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Text(tr("Saqlangan mulklar", "Saved properties", "Избранная недвижимость"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = tr("Sevimli e'lonlarni yig'ish uchun akkauntingizga kiring.", "Sign in to collect favorite listings.", "Войдите, чтобы сохранять понравившиеся объявления."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                ) {
                    androidx.compose.material3.Icon(Icons.Default.Login, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(tr("Kirish", "Sign in", "Войти"), fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onRegister,
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                ) {
                    Text(tr("Ro'yxatdan o'tish", "Register", "Регистрация"), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun EmptySavedState(onBrowse: () -> Unit) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
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
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp))
                    .background(BrandLight),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.TravelExplore,
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(tr("Hali hech narsa saqlanmadi", "Nothing saved yet", "Пока ничего не сохранено"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = tr("Uy yoki kvartira kartasidagi yurak tugmasini bosib saqlashni boshlang.", "Tap the heart on a property card to start saving listings.", "Нажмите на сердечко в карточке, чтобы начать сохранять объявления."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onBrowse,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brand)
            ) {
                Text(tr("E'lonlarni ko'rish", "Browse listings", "Смотреть объявления"), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun LazyGridScope.fullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}
