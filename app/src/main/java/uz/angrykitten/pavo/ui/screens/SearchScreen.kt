package uz.angrykitten.pavo.ui.screens

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
import uz.angrykitten.pavo.ui.components.AnimalCard
import uz.angrykitten.pavo.ui.components.AnimalGridCard
import uz.angrykitten.pavo.ui.components.ListingLayoutToggle
import uz.angrykitten.pavo.ui.localization.tr
import uz.angrykitten.pavo.ui.navigation.Screen
import uz.angrykitten.pavo.ui.theme.Brand
import uz.angrykitten.pavo.ui.viewmodel.AppViewModel
import uz.angrykitten.pavo.ui.viewmodel.FilterState

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

    val animalTypeFilters = listOf(
        null to tr("Barchasi", "All", "Все"),
        "dog" to tr("It", "Dog", "Собака"),
        "cat" to tr("Mushuk", "Cat", "Кошка"),
        "sheep" to tr("Qo'y", "Sheep", "Овца"),
        "cow" to tr("Sigir", "Cow", "Корова"),
        "horse" to tr("Ot", "Horse", "Лошадь")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
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
                            tr("Hayvon, shahar yoki zot bo'yicha qidiring", "Search by animal, city or breed", "Поиск по виду, городу или породе"),
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
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text(tr("Zot, shahar yoki kalit so'z...", "Breed, city or keyword...", "Порода, город или ключевое слово...")) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            AnimatedVisibility(visible = searchQuery.isNotBlank(), enter = scaleIn(tween(200)), exit = scaleOut(tween(200))) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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
                        Icon(Icons.Default.Tune, contentDescription = tr("Filtr", "Filter", "Фильтр"), tint = Color.White)
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    animalTypeFilters.forEach { (type, label) ->
                        AnimatedFilterChip(
                            selected = filterState.animalType == type,
                            label = label,
                            onClick = { viewModel.updateFilter(filterState.copy(animalType = type)) }
                        )
                    }
                }
            }
        }

        when {
            searchResults.isEmpty() -> SearchNoResultsState(
                searchQuery = searchQuery,
                hasActiveFilters = filterState.hasActiveConstraints()
            )
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
                                tr("${searchResults.size} ta e'lon", "${searchResults.size} listings", "${searchResults.size} объявлений"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ListingLayoutToggle(columns = layoutColumns, onColumnsChange = { columns = it })
                        }
                    }

                    items(searchResults, key = { it.id }) { animal ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(tween(350)) { it / 3 } + fadeIn(tween(350))
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
    }

    if (showFilterSheet) {
        AnimalFilterBottomSheet(
            filterState = filterState,
            onApply = { viewModel.updateFilter(it) },
            onReset = { viewModel.clearFilter() },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun SearchNoResultsState(searchQuery: String, hasActiveFilters: Boolean) {
    val title = when {
        searchQuery.isNotBlank() ->
            tr("'$searchQuery' bo'yicha e'lon topilmadi", "No listings for '$searchQuery'", "По запросу '$searchQuery' ничего нет")
        hasActiveFilters ->
            tr("Filtrlarga mos e'lon topilmadi", "No listings match these filters", "Нет объявлений по фильтрам")
        else ->
            tr("Hozircha faol e'lonlar yo'q", "No active listings yet", "Активных объявлений пока нет")
    }
    val body = when {
        searchQuery.isNotBlank() ->
            tr("Boshqa kalit so'z yoki filtrlarni sinab ko'ring", "Try another keyword or filter", "Попробуйте другой запрос или фильтр")
        hasActiveFilters ->
            tr("Filtrlarni yumshatib yana tekshirib ko'ring", "Loosen the filters and try again", "Смягчите фильтры и попробуйте снова")
        else ->
            tr("Keyinroq yana tekshirib ko'ring", "Check back later", "Вернитесь позже")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Icon(Icons.Default.SearchOff, null, modifier = Modifier.padding(24.dp).size(34.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalFilterBottomSheet(
    filterState: FilterState,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedListingType by remember { mutableStateOf(filterState.listingType) }
    var selectedAnimalType by remember { mutableStateOf(filterState.animalType) }
    var minPrice by remember { mutableStateOf(filterState.minPrice?.toInt()?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(filterState.maxPrice?.toInt()?.toString() ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tr("Filtrlar", "Filters", "Фильтры"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
            }

            Spacer(Modifier.height(18.dp))
            Text(tr("Muomala turi", "Listing type", "Тип объявления"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    null to tr("Barchasi", "All", "Все"),
                    "sale" to tr("Sotuv", "Sale", "Продажа"),
                    "adoption" to tr("Asrab olish", "Adoption", "Приютить"),
                    "stud" to tr("Juftlash", "Stud", "Вязка")
                ).forEach { (type, label) ->
                    AnimatedFilterChip(selected = selectedListingType == type, label = label, onClick = { selectedListingType = type })
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(tr("Hayvon turi", "Animal type", "Вид животного"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    null to tr("Barchasi", "All", "Все"),
                    "dog" to tr("It", "Dog", "Собака"),
                    "cat" to tr("Mushuk", "Cat", "Кошка"),
                    "sheep" to tr("Qo'y", "Sheep", "Овца"),
                    "cow" to tr("Sigir", "Cow", "Корова"),
                    "horse" to tr("Ot", "Horse", "Лошадь"),
                    "other" to tr("Boshqa", "Other", "Другое")
                ).forEach { (type, label) ->
                    AnimatedFilterChip(selected = selectedAnimalType == type, label = label, onClick = { selectedAnimalType = type })
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(tr("Narx diapazoni (USD)", "Price range (USD)", "Диапазон цен (USD)"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
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

            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { onReset(); onDismiss() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(tr("Tozalash", "Reset", "Сброс"), fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        onApply(
                            filterState.copy(
                                listingType = selectedListingType,
                                animalType = selectedAnimalType,
                                minPrice = minPrice.toDoubleOrNull(),
                                maxPrice = maxPrice.toDoubleOrNull()
                            )
                        )
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
