package uz.angrykitten.uybek.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.angrykitten.uybek.data.model.Property
import uz.angrykitten.uybek.data.repository.AuthRepository
import uz.angrykitten.uybek.data.repository.AuthResult
import uz.angrykitten.uybek.data.repository.PropertyRepository
import uz.angrykitten.uybek.data.repository.SupabaseRepository
import uz.angrykitten.uybek.data.repository.SupabaseUser
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

/** UI state for authentication operations */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val phoneVerificationId: String? = null,
    val phoneStep: PhoneStep = PhoneStep.ENTER_NUMBER
)

enum class PhoneStep { ENTER_NUMBER, ENTER_OTP }

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val propertyRepo = PropertyRepository(application)
    val userRepo = UserRepository(application)
    private val authRepo = AuthRepository()
    private val supabaseRepo = SupabaseRepository()
    private val dataVersion = MutableStateFlow(0)

    // ─── Auth UI State ───────────────────────────────────────────────────────
    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState

    // ─── DataStore Auth State ────────────────────────────────────────────────
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

    val userPhone: StateFlow<String?> = userRepo.userPhone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ─── Saved properties ────────────────────────────────────────────────────
    val savedIds: StateFlow<Set<String>> = userRepo.savedPropertyIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // ─── Filter state ────────────────────────────────────────────────────────
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    init {
        refreshRemoteContent()
    }

    // ─── Home feed ───────────────────────────────────────────────────────────
    val homeProperties: StateFlow<List<Property>> = combine(
        _filterState, savedIds, dataVersion
    ) { filter, saved, _ ->
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

    val savedProperties: StateFlow<List<Property>> = combine(savedIds, dataVersion) { ids, _ ->
        propertyRepo.getProperties(savedIds = ids, onlySaved = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<List<Property>> = combine(_searchQuery, dataVersion) { q, _ ->
        if (q.isBlank()) emptyList()
        else propertyRepo.getProperties(query = q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ════════════════════════════════════════════════════════════════════════
    // Real Firebase Auth Actions
    // ════════════════════════════════════════════════════════════════════════

    /** Register with email + password via Firebase */
    fun registerWithEmail(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = authRepo.registerWithEmail(email, password)) {
                is AuthResult.Success -> {
                    val user = result.user
                    persistUserLocally(
                        uid = user.uid,
                        name = name.ifBlank { user.displayName ?: "Foydalanuvchi" },
                        email = user.email ?: email,
                        avatar = user.photoUrl?.toString() ?: ""
                    )
                    syncUserToSupabase(uid = user.uid, name = name, email = user.email ?: email)
                    _authUiState.value = _authUiState.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
            }
        }
    }

    /** Login with email + password via Firebase */
    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = authRepo.loginWithEmail(email, password)) {
                is AuthResult.Success -> {
                    val user = result.user
                    persistUserLocally(
                        uid = user.uid,
                        name = user.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                        email = user.email ?: email,
                        avatar = user.photoUrl?.toString() ?: ""
                    )
                    _authUiState.value = _authUiState.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
            }
        }
    }

    /** Sign in with a Google ID Token obtained from Credential Manager */
    fun signInWithGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = authRepo.signInWithGoogle(idToken)) {
                is AuthResult.Success -> {
                    val user = result.user
                    persistUserLocally(
                        uid = user.uid,
                        name = user.displayName ?: "Google Foydalanuvchi",
                        email = user.email ?: "",
                        avatar = user.photoUrl?.toString() ?: ""
                    )
                    syncUserToSupabase(
                        uid = user.uid,
                        name = user.displayName,
                        email = user.email,
                        phone = user.phoneNumber
                    )
                    _authUiState.value = _authUiState.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
            }
        }
    }

    /** Step 1: Send OTP to phone number */
    fun sendPhoneOtp(
        phoneNumber: String,
        activity: Activity,
        onAutoVerified: () -> Unit
    ) {
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        authRepo.sendPhoneOtp(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = { verificationId ->
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    phoneVerificationId = verificationId,
                    phoneStep = PhoneStep.ENTER_OTP
                )
            },
            onAutoVerified = { credential ->
                viewModelScope.launch {
                    signInWithPhoneCredential(
                        credential = credential,
                        onSuccess = {
                            _authUiState.value = AuthUiState()
                            onAutoVerified()
                        },
                        onError = { msg ->
                            _authUiState.value = _authUiState.value.copy(isLoading = false, error = msg)
                        }
                    )
                }
            },
            onError = { msg ->
                _authUiState.value = _authUiState.value.copy(isLoading = false, error = msg)
            }
        )
    }

    /** Step 2: Verify OTP code entered by user */
    fun verifyPhoneOtp(
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val verificationId = _authUiState.value.phoneVerificationId ?: run {
            onError("Tasdiqlash ID topilmadi")
            return
        }
        _authUiState.value = _authUiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = authRepo.verifyPhoneCode(verificationId, code)) {
                is AuthResult.Success -> {
                    val user = result.user
                    persistUserLocally(
                        uid = user.uid,
                        name = user.displayName ?: "Foydalanuvchi",
                        email = user.email ?: "",
                        avatar = user.photoUrl?.toString() ?: ""
                    )
                    syncUserToSupabase(
                        uid = user.uid,
                        name = user.displayName,
                        email = user.email,
                        phone = user.phoneNumber
                    )
                    _authUiState.value = AuthUiState()
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
            }
        }
    }

    private suspend fun signInWithPhoneCredential(
        credential: PhoneAuthCredential,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        when (val result = authRepo.signInWithPhoneCredential(credential)) {
            is AuthResult.Success -> {
                val user = result.user
                persistUserLocally(
                    uid = user.uid,
                    name = user.displayName ?: "Foydalanuvchi",
                    email = user.email ?: "",
                    avatar = user.photoUrl?.toString() ?: ""
                )
                syncUserToSupabase(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email,
                    phone = user.phoneNumber,
                    avatarUrl = user.photoUrl?.toString()
                )
                onSuccess()
            }
            is AuthResult.Error -> onError(result.message)
        }
    }

    fun resetPhoneStep() {
        _authUiState.value = AuthUiState()
    }

    fun sendPasswordReset(email: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepo.resetPassword(email)
            onDone(result.isSuccess)
        }
    }

    fun clearAuthError() {
        _authUiState.value = _authUiState.value.copy(error = null)
    }

    // ─── Sign Out ────────────────────────────────────────────────────────────

    fun signOut() {
        authRepo.signOut()
        viewModelScope.launch { userRepo.signOut() }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch { userRepo.updateName(name) }
    }

    fun updateUserPhone(phone: String) {
        viewModelScope.launch { userRepo.updatePhone(phone) }
    }

    fun deleteAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepo.signOut()
            userRepo.signOut()
            onDone()
        }
    }

    // ─── Legacy signIn (kept for compatibility) ───────────────────────────────
    @Deprecated("Use loginWithEmail / registerWithEmail instead")
    fun signIn(userId: String = "local_user", name: String, email: String, avatar: String = "") {
        viewModelScope.launch {
            userRepo.signIn(userId, name, email, avatar)
        }
    }

    // ─── Internal helpers ────────────────────────────────────────────────────

    private suspend fun persistUserLocally(uid: String, name: String, email: String, avatar: String) {
        userRepo.signIn(userId = uid, name = name, email = email, avatar = avatar)
    }

    private fun syncUserToSupabase(
        uid: String,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        avatarUrl: String? = null
    ) {
        viewModelScope.launch {
            supabaseRepo.upsertUser(
                SupabaseUser(
                    id = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    avatar_url = avatarUrl
                )
            )
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Toggle saved / Filter / Post
    // ════════════════════════════════════════════════════════════════════════

    fun toggleSaved(propertyId: String) {
        viewModelScope.launch { userRepo.toggleSaved(propertyId) }
    }

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
    fun postListing(property: Property) {
        viewModelScope.launch {
            propertyRepo.addProperty(property)
            dataVersion.value = dataVersion.value + 1
        }
    }
    fun deleteProperty(id: String) {
        viewModelScope.launch {
            propertyRepo.deleteProperty(id)
            dataVersion.value = dataVersion.value + 1
        }
    }
    fun getUserProperties(): List<Property> {
        val uid = userId.value ?: return emptyList()
        return propertyRepo.getUserProperties(uid)
    }

    suspend fun uploadImage(uri: android.net.Uri): Result<String> {
        return propertyRepo.uploadImage(uri)
    }

    fun getAllProperties(): List<Property> = propertyRepo.getProperties()
    fun getPropertyById(id: String): Property? = propertyRepo.getPropertyById(id)
    fun getCities() = propertyRepo.getCities()
    fun getDistricts(cityId: Int?) = propertyRepo.getDistricts(cityId)

    private fun refreshRemoteContent() {
        viewModelScope.launch {
            propertyRepo.refreshFromSupabase(force = true)
            dataVersion.value = dataVersion.value + 1
        }
    }
}
