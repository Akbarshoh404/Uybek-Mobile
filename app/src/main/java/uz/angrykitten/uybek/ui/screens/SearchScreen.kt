package uz.angrykitten.uybek.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: AppViewModel, navController: NavController) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedIds.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var columns by rememberSaveable { mutableIntStateOf(2) }
    val layoutColumns = if (columns == 1) 1 else 2

    val propertyTypes = listOf(
        null to tr("Barchasi", "All", "Все"),
        "apartment" to tr("Kvartira", "Apartment", "Квартира"),
        "house" to tr("Uy", "House", "Дом"),
        "commercial" to tr("Tijorat", "Commercial", "Коммерция"),
        "land" to tr("Yer", "Land", "Участок")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledIconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Orqaga", "Back", "Назад"))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            tr("Qidiruv", "Search", "Поиск"),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            tr("Natijalarni uslub va zichlik bilan boshqaring", "Control result style and density", "Управляйте видом и плотностью результатов"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(tr("Shahar, tuman yoki kalit so'z...", "City, district or keyword...", "Город, район или ключевое слово...")) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = searchQuery.isNotBlank(),
                                enter = scaleIn(tween(200)),
                                exit = scaleOut(tween(200))
                            ) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Brand,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        singleLine = true
                    )

                    FilledIconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Brand)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = tr("Filter", "Filter", "Фильтр"), tint = Color.White)
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    propertyTypes.forEach { (type, label) ->
                        AnimatedFilterChip(
                            selected = filterState.propertyType == type,
                            label = label,
                            onClick = { viewModel.setPropertyTypeFilter(type) }
                        )
                    }
                }
            }
        }

        when {
            searchQuery.isBlank() -> SearchEmptyState()
            searchResults.isEmpty() -> SearchNoResultsState(searchQuery)
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(layoutColumns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                tr("${searchResults.size} ta natija topildi", "${searchResults.size} results found", "Найдено результатов: ${searchResults.size}"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ListingLayoutToggle(columns = layoutColumns, onColumnsChange = { columns = it })
                        }
                    }

                    items(searchResults, key = { it.id }) { property ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(tween(350)) { it / 3 } + fadeIn(tween(350))
                        ) {
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
    }

    if (showFilterSheet) {
        ModernFilterBottomSheet(
            viewModel = viewModel,
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun SearchEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.padding(24.dp).size(34.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            tr("Ko'chmas mulk qidiring", "Search real estate", "Ищите недвижимость"),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            tr("Shahar, tuman yoki mulk nomini kiriting", "Enter a city, district, or property name", "Введите город, район или название объекта"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchNoResultsState(searchQuery: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.padding(24.dp).size(34.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            tr("'$searchQuery' bo'yicha e'lon topilmadi", "No listings found for '$searchQuery'", "Объявления по запросу '$searchQuery' не найдены"),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            tr("Boshqa kalit so'z yoki filtrlarni sinab ko'ring", "Try another keyword or filter", "Попробуйте другой запрос или фильтр"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFilterBottomSheet(viewModel: AppViewModel, onDismiss: () -> Unit) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var selectedDeal by remember { mutableStateOf(filterState.dealType) }
    var selectedType by remember { mutableStateOf(filterState.propertyType) }
    var minPrice by remember { mutableStateOf(filterState.minPrice?.toInt()?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(filterState.maxPrice?.toInt()?.toString() ?: "") }
    var selectedBedrooms by remember { mutableStateOf(filterState.bedrooms) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tr("Filtrlar", "Filters", "Фильтры"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
            }

            Spacer(Modifier.height(18.dp))
            FilterSection(tr("Muomala turi", "Deal type", "Тип сделки"))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    null to tr("Barchasi", "All", "Все"),
                    "sale" to tr("Sotiladi", "For sale", "Продажа"),
                    "rent" to tr("Ijaraga", "For rent", "Аренда")
                ).forEach { (type, label) ->
                    AnimatedFilterChip(selected = selectedDeal == type, label = label, onClick = { selectedDeal = type })
                }
            }

            Spacer(Modifier.height(18.dp))
            FilterSection(tr("Mulk turi", "Property type", "Тип недвижимости"))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    null to tr("Barchasi", "All", "Все"),
                    "apartment" to tr("Kvartira", "Apartment", "Квартира"),
                    "house" to tr("Uy", "House", "Дом"),
                    "commercial" to tr("Tijorat", "Commercial", "Коммерция"),
                    "land" to tr("Yer", "Land", "Участок")
                ).forEach { (type, label) ->
                    AnimatedFilterChip(selected = selectedType == type, label = label, onClick = { selectedType = type })
                }
            }

            Spacer(Modifier.height(18.dp))
            FilterSection(tr("Narx diapazoni (USD)", "Price range (USD)", "Диапазон цен (USD)"))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it.filter(Char::isDigit) },
                    label = { Text(tr("Min", "Min", "Мин")) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand),
                    singleLine = true
                )
                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it.filter(Char::isDigit) },
                    label = { Text(tr("Max", "Max", "Макс")) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(18.dp))
            FilterSection(tr("Xonalar soni", "Bedrooms", "Комнаты"))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to tr("Barchasi", "All", "Все"), 1 to "1", 2 to "2", 3 to "3", 4 to "4+").forEach { (b, label) ->
                    AnimatedFilterChip(selected = selectedBedrooms == b, label = label, onClick = { selectedBedrooms = b })
                }
            }

            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetFilters()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(tr("Tozalash", "Reset", "Сброс"), fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        viewModel.setDealTypeFilter(selectedDeal)
                        viewModel.setPropertyTypeFilter(selectedType)
                        viewModel.setPriceRange(minPrice.toDoubleOrNull(), maxPrice.toDoubleOrNull())
                        viewModel.setBedroomFilter(selectedBedrooms)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                ) {
                    Text(tr("Qo'llash", "Apply", "Применить"), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}
