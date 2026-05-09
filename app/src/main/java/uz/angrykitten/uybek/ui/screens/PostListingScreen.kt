package uz.angrykitten.uybek.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import uz.angrykitten.uybek.data.model.Property
import uz.angrykitten.uybek.ui.navigation.Screen
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListingScreen(viewModel: AppViewModel, navController: NavController) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        GuestPromptScreen(
            title = "E'lon berish",
            message = "E'lon berish uchun tizimga kirish zarur",
            navController = navController
        )
        return
    }

    // Step state
    var currentStep by remember { mutableStateOf(0) }
    val totalSteps = 5

    // Form state
    var dealType by remember { mutableStateOf("sale") }
    var propertyType by remember { mutableStateOf("apartment") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCityId by remember { mutableStateOf<Int?>(null) }
    var selectedCityName by remember { mutableStateOf("") }
    var selectedDistrictId by remember { mutableStateOf<Int?>(null) }
    var selectedDistrictName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var totalFloors by remember { mutableStateOf("") }
    var yearBuilt by remember { mutableStateOf("") }
    var isNewBuild by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    var pricePeriod by remember { mutableStateOf<String?>(null) }
    var phone by remember { mutableStateOf("") }
    var telegram by remember { mutableStateOf("") }
    var stepError by remember { mutableStateOf<String?>(null) }
    
    val uploadedImages = remember { mutableStateListOf<String>() }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()
    val cities = viewModel.getCities()

    // Auto-fill phone from user profile on first composition
    LaunchedEffect(userPhone) {
        if (phone.isBlank() && !userPhone.isNullOrBlank()) {
            phone = userPhone!!.removePrefix("+998")
        }
    }

    val stepTitles = listOf("Muomala turi", "Mulk haqida", "Joylashuv", "Narx va aloqa", "Sharh")

    // Required field validation per step
    fun canProceed(): Pair<Boolean, String?> = when (currentStep) {
        2 -> if (selectedCityId != null) true to null else false to "Shaharni tanlash majburiy"
        3 -> when {
            price.isBlank() -> false to "Narxni kiriting"
            phone.isBlank() -> false to "Telefon raqamni kiriting"
            else -> true to null
        }
        else -> true to null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "E'lon berish",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    if (index <= currentStep) Brand else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "${currentStep + 1}/$totalSteps: ${stepTitles[currentStep]}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Step content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when (currentStep) {
                0 -> StepDealAndType(dealType, propertyType, onDealType = { dealType = it }, onPropertyType = { propertyType = it })
                1 -> StepDetails(title, description, bedrooms, bathrooms, area, floor, totalFloors, yearBuilt, isNewBuild,
                    uploadedImages, isUploading,
                    onTitle = { title = it }, onDescription = { description = it },
                    onBedrooms = { bedrooms = it }, onBathrooms = { bathrooms = it },
                    onArea = { area = it }, onFloor = { floor = it }, onTotalFloors = { totalFloors = it },
                    onYearBuilt = { yearBuilt = it }, onIsNewBuild = { isNewBuild = it },
                    onImagePicked = { uri ->
                        scope.launch {
                            isUploading = true
                            viewModel.uploadImage(uri).onSuccess { url ->
                                uploadedImages.add(url)
                            }.onFailure {
                                stepError = "Rasm yuklashda xatolik: ${it.message}"
                            }
                            isUploading = false
                        }
                    },
                    onRemoveImage = { url -> uploadedImages.remove(url) }
                )
                2 -> StepLocation(cities, selectedCityId, selectedCityName, selectedDistrictId, selectedDistrictName, address,
                    viewModel = viewModel,
                    onCitySelected = { id, name -> selectedCityId = id; selectedCityName = name; selectedDistrictId = null; selectedDistrictName = "" },
                    onDistrictSelected = { id, name -> selectedDistrictId = id; selectedDistrictName = name },
                    onAddress = { address = it })
                3 -> StepPriceContact(price, currency, pricePeriod, phone, telegram, dealType,
                    onPrice = { price = it }, onCurrency = { currency = it },
                    onPricePeriod = { pricePeriod = it }, onPhone = { phone = it }, onTelegram = { telegram = it })
                4 -> StepReview(dealType, propertyType, title, selectedCityName, selectedDistrictName, area, bedrooms, price, currency, pricePeriod)
            }
            // Step error message
            stepError?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Navigation buttons
        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(6.dp))
                        Text("Orqaga", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            val (ok, err) = canProceed()
                            if (ok) { currentStep++; stepError = null }
                            else stepError = err
                        } else {
                            // Submit
                            val property = Property(
                                id = UUID.randomUUID().toString(),
                                user_id = userId ?: "local_user",
                                title = title.ifBlank { "$selectedCityName, ${propertyType.replaceFirstChar { it.uppercase() }}" },
                                description = description,
                                deal_type = dealType,
                                property_type = propertyType,
                                city_id = selectedCityId ?: 1,
                                city_name = selectedCityName.ifBlank { "Toshkent" },
                                district_id = selectedDistrictId ?: 1,
                                district_name = selectedDistrictName.ifBlank { "" },
                                address = address,
                                latitude = 41.3,
                                longitude = 69.2,
                                price = price.toDoubleOrNull() ?: 0.0,
                                currency = currency,
                                price_period = pricePeriod,
                                area_m2 = area.toDoubleOrNull() ?: 0.0,
                                bedrooms = bedrooms.toIntOrNull() ?: 0,
                                bathrooms = bathrooms.toIntOrNull() ?: 0,
                                floor = floor.toIntOrNull() ?: 0,
                                total_floors = totalFloors.toIntOrNull() ?: 0,
                                year_built = yearBuilt.toIntOrNull() ?: 0,
                                is_new_build = isNewBuild,
                                is_active = true,
                                views_count = 0,
                                images = if (uploadedImages.isEmpty()) listOf("https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800") else uploadedImages.toList(),
                                seller_name = userName ?: "Foydalanuvchi",
                                seller_phone = if (phone.isNotBlank()) "+998$phone" else "",
                                seller_whatsapp = if (telegram.isNotBlank()) telegram else "",
                                seller_avatar = ""
                            )
                            viewModel.postListing(property)
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.PostListing.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text(
                        if (currentStep == totalSteps - 1) "E'lon berish" else "Keyingisi",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.surface
                    )
                    if (currentStep < totalSteps - 1) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.surface)
                    }
                }
            }
        }
    }
}

@Composable
fun StepDealAndType(
    dealType: String, propertyType: String,
    onDealType: (String) -> Unit, onPropertyType: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column {
            Text("Muomala turi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DealTypeCard(
                    icon = Icons.Default.Sell,
                    title = "Sotiladi",
                    selected = dealType == "sale",
                    onClick = { onDealType("sale") },
                    modifier = Modifier.weight(1f)
                )
                DealTypeCard(
                    icon = Icons.Default.Key,
                    title = "Ijaraga",
                    selected = dealType == "rent",
                    onClick = { onDealType("rent") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Column {
            Text("Mulk turi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            val types = listOf(
                Triple(Icons.Default.Apartment, "apartment", "Kvartira"),
                Triple(Icons.Default.Home, "house", "Uy"),
                Triple(Icons.Default.Business, "commercial", "Tijorat"),
                Triple(Icons.Default.Terrain, "land", "Yer")
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                types.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { (icon, type, label) ->
                            DealTypeCard(
                                icon = icon,
                                title = label,
                                selected = propertyType == type,
                                onClick = { onPropertyType(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DealTypeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) 
                 else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun StepDetails(
    title: String, description: String, bedrooms: String, bathrooms: String,
    area: String, floor: String, totalFloors: String, yearBuilt: String, isNewBuild: Boolean,
    uploadedImages: List<String>, isUploading: Boolean,
    onTitle: (String) -> Unit, onDescription: (String) -> Unit,
    onBedrooms: (String) -> Unit, onBathrooms: (String) -> Unit,
    onArea: (String) -> Unit, onFloor: (String) -> Unit, onTotalFloors: (String) -> Unit,
    onYearBuilt: (String) -> Unit, onIsNewBuild: (Boolean) -> Unit,
    onImagePicked: (android.net.Uri) -> Unit,
    onRemoveImage: (String) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImagePicked(it) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Mulk tafsilotlari", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        
        // Image Upload Section
        Text("Rasmlar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(uploadedImages.size) { index ->
                val url = uploadedImages[index]
                Box(modifier = Modifier.size(100.dp)) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Uploaded Image",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onRemoveImage(url) },
                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Close, "O'chirish", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            item {
                if (isUploading) {
                    Box(modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Brand)
                    }
                } else if (uploadedImages.size < 10) {
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text("Qo'shish", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        
        PostFormField("E'lon sarlavhasi", title, onTitle)
        PostFormField("Tavsif", description, onDescription, singleLine = false, minLines = 3)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PostFormField("Xonalar", bedrooms, onBedrooms, KeyboardType.Number, modifier = Modifier.weight(1f))
            PostFormField("Hammomlar", bathrooms, onBathrooms, KeyboardType.Number, modifier = Modifier.weight(1f))
        }
        PostFormField("Maydon (m²)", area, onArea, KeyboardType.Number)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PostFormField("Qavat", floor, onFloor, KeyboardType.Number, modifier = Modifier.weight(1f))
            PostFormField("Jami qavat", totalFloors, onTotalFloors, KeyboardType.Number, modifier = Modifier.weight(1f))
        }
        PostFormField("Qurilish yili", yearBuilt, onYearBuilt, KeyboardType.Number)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isNewBuild, onCheckedChange = onIsNewBuild, colors = CheckboxDefaults.colors(checkedColor = Brand))
            Text("Yangi qurilish", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepLocation(
    cities: List<uz.angrykitten.uybek.data.model.City>,
    selectedCityId: Int?,
    selectedCityName: String,
    selectedDistrictId: Int?,
    selectedDistrictName: String,
    address: String,
    viewModel: AppViewModel,
    onCitySelected: (Int, String) -> Unit,
    onDistrictSelected: (Int, String) -> Unit,
    onAddress: (String) -> Unit
) {
    val districts = remember(selectedCityId) { viewModel.getDistricts(selectedCityId) }
    var cityExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Joylashuv", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        // City dropdown
        ExposedDropdownMenuBox(expanded = cityExpanded, onExpandedChange = { cityExpanded = it }) {
            OutlinedTextField(
                value = selectedCityName.ifBlank { "Shaharni tanlang" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Shahar") },
                leadingIcon = { Icon(Icons.Default.LocationCity, null, tint = Brand) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(cityExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.onSurface)
            )
            ExposedDropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city.name) },
                        onClick = { onCitySelected(city.id, city.name); cityExpanded = false }
                    )
                }
            }
        }

        // District dropdown
        ExposedDropdownMenuBox(expanded = districtExpanded, onExpandedChange = { if (selectedCityId != null) districtExpanded = it }) {
            OutlinedTextField(
                value = selectedDistrictName.ifBlank { "Tumanni tanlang" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Tuman") },
                leadingIcon = { Icon(Icons.Default.Map, null, tint = Brand) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(districtExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.onSurface),
                enabled = selectedCityId != null
            )
            ExposedDropdownMenu(expanded = districtExpanded, onDismissRequest = { districtExpanded = false }) {
                districts.forEach { district ->
                    DropdownMenuItem(
                        text = { Text(district.name) },
                        onClick = { onDistrictSelected(district.id, district.name); districtExpanded = false }
                    )
                }
            }
        }

        PostFormField("Aniq manzil (ixtiyoriy)", address, onAddress)
    }
}

@Composable
fun StepPriceContact(
    price: String, currency: String, pricePeriod: String?, phone: String, telegram: String, dealType: String,
    onPrice: (String) -> Unit, onCurrency: (String) -> Unit,
    onPricePeriod: (String?) -> Unit, onPhone: (String) -> Unit, onTelegram: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Narx va aloqa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        PostFormField("Narx *", price, onPrice, KeyboardType.Number)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("USD" to "$", "UZS" to "so'm").forEach { (c, label) ->
                FilterChip(
                    selected = currency == c,
                    onClick = { onCurrency(c) },
                    label = { Text("$c ($label)") },
                    shape = RoundedCornerShape(18.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.onSurface, selectedLabelColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
        if (dealType == "rent") {
            Text("Ijara davri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("month" to "Oylik", "year" to "Yillik").forEach { (p, label) ->
                    FilterChip(
                        selected = pricePeriod == p,
                        onClick = { onPricePeriod(p) },
                        label = { Text(label) },
                        shape = RoundedCornerShape(18.dp),
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.onSurface, selectedLabelColor = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }

        // Phone with +998 prefix
        Text("Aloqa ma'lumotlari", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        PhoneFieldUz("Telefon raqam *", phone, onPhone)
        PostFormField("Telegram (@username)", telegram, onTelegram)
    }
}

@Composable
fun PhoneFieldUz(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Only allow digits, max 9 after +998
            val digits = input.filter { it.isDigit() }.take(9)
            onValueChange(digits)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        prefix = {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    "+998",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        placeholder = { Text("XX XXX XX XX") },
        leadingIcon = {
            Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        },
        supportingText = {
            val digits = value.filter { it.isDigit() }
            Text(
                if (digits.isNotEmpty()) "+998 ${
                    digits.chunked(2).joinToString(" ")
                }" else "Masalan: 90 123 45 67",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
fun StepReview(
    dealType: String, propertyType: String, title: String,
    cityName: String, districtName: String, area: String,
    bedrooms: String, price: String, currency: String, pricePeriod: String?
) {
    val dealLabel = if (dealType == "sale") "Sotiladi" else "Ijaraga"
    val typeLabel = when (propertyType) {
        "apartment" -> "Kvartira"
        "house" -> "Uy"
        "commercial" -> "Tijorat"
        "land" -> "Yer"
        else -> propertyType
    }
    val location = listOfNotNull(
        districtName.ifBlank { null },
        cityName.ifBlank { null }
    ).joinToString(", ").ifBlank { "(kiritilmagan)" }
    val priceText = if (price.isNotBlank())
        "$price $currency${if (pricePeriod == "month") "/oy" else if (pricePeriod == "year") "/yil" else ""}"
    else "(kiritilmagan)"

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            "E'lon ko'rib chiqish",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // Summary card
        Card(
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                ReviewIconRow(Icons.Default.Sell, "Muomala turi", dealLabel)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), thickness = 1.dp)
                ReviewIconRow(Icons.Default.Home, "Mulk turi", typeLabel)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), thickness = 1.dp)
                ReviewIconRow(Icons.Default.TextFields, "Sarlavha", title.ifBlank { "(kiritilmagan)" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), thickness = 1.dp)
                ReviewIconRow(Icons.Default.LocationOn, "Joylashuv", location)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), thickness = 1.dp)
                if (area.isNotBlank()) {
                    ReviewIconRow(Icons.Default.SquareFoot, "Maydon", "$area m²")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), thickness = 1.dp)
                }
                if (bedrooms.isNotBlank()) {
                    ReviewIconRow(Icons.Default.KingBed, "Xonalar", "$bedrooms ta")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), thickness = 1.dp)
                }
                ReviewIconRow(Icons.Default.AttachMoney, "Narx", priceText)
            }
        }

        // Info banner
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        "Tayyor!",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "E'lon berish tugmasini bosganingizdan so'ng e'lon darhol faollashadi va ko'rib chiqiladi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ReviewIconRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PostFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines
    )
}
