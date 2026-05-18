package uz.angrykitten.pavo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import uz.angrykitten.pavo.ui.components.ListingLayoutToggle
import uz.angrykitten.pavo.ui.components.AnimalCard
import uz.angrykitten.pavo.ui.components.AnimalGridCard
import uz.angrykitten.pavo.ui.localization.tr
import uz.angrykitten.pavo.ui.navigation.Screen
import uz.angrykitten.pavo.ui.theme.Brand
import uz.angrykitten.pavo.ui.theme.GradientEnd
import uz.angrykitten.pavo.ui.theme.GradientStart
import uz.angrykitten.pavo.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AppViewModel, navController: NavController) {
    val animals by viewModel.homeAnimals.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    var columns by rememberSaveable { mutableIntStateOf(1) }
    val layoutColumns = if (columns == 1) 1 else 2

    val listingFilters = listOf(
        null to tr("Barchasi", "All", "Все"),
        "sale" to tr("Sotiladi", "For sale", "Продажа"),
        "adoption" to tr("Asrab olish", "Adoption", "Приютить"),
        "stud" to tr("Juftlash", "Stud", "Вязка")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(layoutColumns),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tr("Xush kelibsiz", "Welcome", "Добро пожаловать"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isLoggedIn && userName != null) "${userName!!.split(" ").first()}!" else tr("Pavo Dunyosi", "Pavo World", "Мир Pavo"),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                FilledIconButton(
                    onClick = {},
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = tr("Bildirishnomalar", "Notifications", "Уведомления"))
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                onClick = { navController.navigate(Screen.Search.route) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = tr("Hayvonlarni qidirish...", "Search animals...", "Поиск животных..."),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                            shape = RoundedCornerShape(30.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                        Text(
                            tr("Pavo Select", "Pavo Select", "Pavo Select"),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            tr("Eng yaxshi do'stlar, ishonchli e'lonlar.", "Best friends, trusted listings.", "Лучшие друзья, надежные объявления."),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(78.dp)
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listingFilters.forEach { (type, label) ->
                    val selected = filterState.listingType == type
                    AnimatedFilterChip(
                        selected = selected,
                        label = label,
                        onClick = { viewModel.updateFilter(filterState.copy(listingType = type)) }
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        tr("E'lonlar", "Listings", "Объявления"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        tr("${animals.size} ta mos natija", "${animals.size} matching results", "${animals.size} подходящих результатов"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ListingLayoutToggle(
                    columns = layoutColumns,
                    onColumnsChange = { columns = it }
                )
            }
        }

        if (animals.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyHomeState()
            }
        } else {
            items(animals, key = { it.id }) { animal ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(tween(400)) { it / 3 } + fadeIn(tween(400))
                ) {
                    if (layoutColumns == 1) {
                        AnimalCard(
                            animal = animal,
                            isSaved = animal.id in savedIds,
                            onCardClick = { navController.navigate(Screen.AnimalDetail.createRoute(animal.id)) },
                            onToggleSave = {
                                if (isLoggedIn) viewModel.toggleSaved(animal.id)
                                else navController.navigate(Screen.Login.route)
                            }
                        )
                    } else {
                        AnimalGridCard(
                            animal = animal,
                            isSaved = animal.id in savedIds,
                            onCardClick = { navController.navigate(Screen.AnimalDetail.createRoute(animal.id)) },
                            onToggleSave = {
                                if (isLoggedIn) viewModel.toggleSaved(animal.id)
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
private fun EmptyHomeState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.padding(24.dp).size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            tr("E'lonlar topilmadi", "No listings found", "Объявления не найдены"),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            tr("Boshqa filtrlarni sinab ko'ring", "Try different filters", "Попробуйте другие фильтры"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AnimatedFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Brand,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            selectedBorderColor = Brand
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
    )
}
