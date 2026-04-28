package uz.angrykitten.uybek.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import uz.angrykitten.uybek.ui.components.PropertyListCard
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

    var showFilterSheet by remember { mutableStateOf(false) }
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    val propertyTypes = listOf(
        null to "Barchasi",
        "apartment" to "Kvartira",
        "house" to "Uy",
        "commercial" to "Tijorat",
        "land" to "Yer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Enhanced Search Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Qidiruv",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))

                // Search Bar with Enhanced Design
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Shahar, tuman yoki kalit so'z...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = Brand,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = searchQuery.isNotBlank(),
                                enter = scaleIn(tween(200)),
                                exit = scaleOut(tween(200))
                            ) {
                                IconButton(
                                    onClick = { viewModel.setSearchQuery("") },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Brand,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        singleLine = true
                    )

                    // Filter Button
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brand)
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Property Type Chips
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    propertyTypes.forEach { (type, label) ->
                        val selected = filterState.propertyType == type
                        AnimatedFilterChip(
                            selected = selected,
                            label = label,
                            onClick = { viewModel.setPropertyTypeFilter(type) }
                        )
                    }
                }
            }
        }

        // Results Section
        when {
            searchQuery.isBlank() -> {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Brand.copy(alpha = 0.3f)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Ko'chmas mulk qidiring",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Shahar, tuman yoki mulk nomini kiriting",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            searchResults.isEmpty() -> {
                // No Results State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "'$searchQuery' bo'yicha e'lon topilmadi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Boshqa kalit so'z yoki filtrlarni sinab ko'ring",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            else -> {
                // Results List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "${searchResults.size} ta natija topildi",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(searchResults, key = { it.id }) { property ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(tween(400)) { it / 2 } + fadeIn(tween(400))
                        ) {
                            PropertyListCard(
                                property = property,
                                isSaved = property.id in savedIds,
                                onCardClick = {
                                    navController.navigate(Screen.PropertyDetail.createRoute(property.id))
                                },
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

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModernFilterBottomSheet(
            viewModel = viewModel,
            onDismiss = { showFilterSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernFilterBottomSheet(viewModel: AppViewModel, onDismiss: () -> Unit) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val cities = viewModel.getCities()

    var selectedDeal by remember { mutableStateOf(filterState.dealType) }
    var selectedType by remember { mutableStateOf(filterState.propertyType) }
    var selectedCityId by remember { mutableStateOf(filterState.cityId) }
    var minPrice by remember { mutableStateOf(filterState.minPrice?.toInt()?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(filterState.maxPrice?.toInt()?.toString() ?: "") }
    var selectedBedrooms by remember { mutableStateOf(filterState.bedrooms) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filtrlar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Deal Type
            Text(
                "Muomala turi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to "Barchasi", "sale" to "Sotiladi", "rent" to "Ijaraga").forEach { (type, label) ->
                    AnimatedFilterChip(
                        selected = selectedDeal == type,
                        label = label,
                        onClick = { selectedDeal = type }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Property Type
            Text(
                "Mulk turi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    null to "Barchasi",
                    "apartment" to "Kvartira",
                    "house" to "Uy",
                    "commercial" to "Tijorat",
                    "land" to "Yer"
                ).forEach { (type, label) ->
                    AnimatedFilterChip(
                        selected = selectedType == type,
                        label = label,
                        onClick = { selectedType = type }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Price Range
            Text(
                "Narx diapazoni (USD)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it.filter { c -> c.isDigit() } },
                    label = { Text("Min") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Brand,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it.filter { c -> c.isDigit() } },
                    label = { Text("Max") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Brand,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(20.dp))

            // Bedrooms
            Text(
                "Xonalar soni",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to "Barchasi", 1 to "1", 2 to "2", 3 to "3", 4 to "4+").forEach { (b, label) ->
                    AnimatedFilterChip(
                        selected = selectedBedrooms == b,
                        label = label,
                        onClick = { selectedBedrooms = b }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetFilters()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tozalash", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        viewModel.setDealTypeFilter(selectedDeal)
                        viewModel.setPropertyTypeFilter(selectedType)
                        viewModel.setCityFilter(selectedCityId)
                        viewModel.setPriceRange(
                            minPrice.toDoubleOrNull(),
                            maxPrice.toDoubleOrNull()
                        )
                        viewModel.setBedroomFilter(selectedBedrooms)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand)
                ) {
                    Text("Qo'llash", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
