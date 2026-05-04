package uz.angrykitten.uybek.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "uybek_prefs")

class UserRepository(private val context: Context) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_AVATAR = stringPreferencesKey("user_avatar")
        private val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        private val KEY_SAVED_IDS = stringSetPreferencesKey("saved_property_ids")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID] != null
    }

    val userId: Flow<String?> = context.dataStore.data.map { it[KEY_USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[KEY_USER_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[KEY_USER_EMAIL] }
    val userAvatar: Flow<String?> = context.dataStore.data.map { it[KEY_USER_AVATAR] }
    val userPhone: Flow<String?> = context.dataStore.data.map { it[KEY_USER_PHONE] }

    val savedPropertyIds: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_SAVED_IDS] ?: emptySet()
    }

    suspend fun signIn(userId: String, name: String, email: String, avatar: String = "", phone: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_AVATAR] = avatar
            if (phone.isNotBlank()) prefs[KEY_USER_PHONE] = phone
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_USER_EMAIL)
            prefs.remove(KEY_USER_AVATAR)
            prefs.remove(KEY_USER_PHONE)
        }
    }

    suspend fun toggleSaved(propertyId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_SAVED_IDS]?.toMutableSet() ?: mutableSetOf()
            if (propertyId in current) current.remove(propertyId) else current.add(propertyId)
            prefs[KEY_SAVED_IDS] = current
        }
    }

    suspend fun updateName(name: String) {
        context.dataStore.edit { prefs -> prefs[KEY_USER_NAME] = name }
    }

    suspend fun updatePhone(phone: String) {
        context.dataStore.edit { prefs -> prefs[KEY_USER_PHONE] = phone }
    }
}

