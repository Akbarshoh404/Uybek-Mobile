package uz.angrykitten.uybek.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.angrykitten.uybek.data.model.Property
import uz.angrykitten.uybek.data.repository.PropertyRepository
import uz.angrykitten.uybek.data.repository.UserRepository

data class FilterState(
    val dealType: String? = null,
    val propertyType: String? = null,
    val cityId: Int? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val bedrooms: Int? = null,
    val query: String = ""
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val propertyRepo = PropertyRepository(application)
    val userRepo = UserRepository(application)

    // --- Auth state ---
    val isLoggedIn: StateFlow<Boolean> = userRepo.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName: StateFlow<String?> = userRepo.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userEmail: StateFlow<String?> = userRepo.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userAvatar: StateFlow<String?> = userRepo.userAvatar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userId: StateFlow<String?> = userRepo.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Saved properties ---
    val savedIds: StateFlow<Set<String>> = userRepo.savedPropertyIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // --- Filter state ---
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    // --- Home feed ---
    val homeProperties: StateFlow<List<Property>> = combine(
        _filterState, savedIds
    ) { filter, saved ->
        propertyRepo.getProperties(
            dealType = filter.dealType,
            propertyType = filter.propertyType,
            cityId = filter.cityId,
            minPrice = filter.minPrice,
            maxPrice = filter.maxPrice,
            bedrooms = filter.bedrooms,
            query = filter.query.ifBlank { null }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), propertyRepo.getProperties())

    // --- Saved properties list ---
    val savedProperties: StateFlow<List<Property>> = savedIds.combine(savedIds) { ids, _ ->
        propertyRepo.getProperties(savedIds = ids, onlySaved = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search query ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<List<Property>> = _searchQuery.combine(_searchQuery) { q, _ ->
        if (q.isBlank()) emptyList()
        else propertyRepo.getProperties(query = q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Auth actions ---
    fun signIn(userId: String = "local_user", name: String, email: String, avatar: String = "") {
        viewModelScope.launch {
            userRepo.signIn(userId, name, email, avatar)
        }
    }

    fun signOut() {
        viewModelScope.launch { userRepo.signOut() }
    }

    // --- Toggle saved ---
    fun toggleSaved(propertyId: String) {
        viewModelScope.launch { userRepo.toggleSaved(propertyId) }
    }

    // --- Filter actions ---
    fun setDealTypeFilter(dealType: String?) {
        _filterState.value = _filterState.value.copy(dealType = dealType)
    }

    fun setPropertyTypeFilter(type: String?) {
        _filterState.value = _filterState.value.copy(propertyType = type)
    }

    fun setCityFilter(cityId: Int?) {
        _filterState.value = _filterState.value.copy(cityId = cityId)
    }

    fun setPriceRange(min: Double?, max: Double?) {
        _filterState.value = _filterState.value.copy(minPrice = min, maxPrice = max)
    }

    fun setBedroomFilter(bedrooms: Int?) {
        _filterState.value = _filterState.value.copy(bedrooms = bedrooms)
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
        _filterState.value = _filterState.value.copy(query = q)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // --- Post listing ---
    fun postListing(property: Property) {
        propertyRepo.addProperty(property)
    }

    fun deleteProperty(id: String) {
        propertyRepo.deleteProperty(id)
    }

    fun getUserProperties(): List<Property> {
        val uid = userId.value ?: return emptyList()
        return propertyRepo.getUserProperties(uid)
    }

    fun getPropertyById(id: String): Property? = propertyRepo.getPropertyById(id)

    fun getCities() = propertyRepo.getCities()
    fun getDistricts(cityId: Int?) = propertyRepo.getDistricts(cityId)
}
