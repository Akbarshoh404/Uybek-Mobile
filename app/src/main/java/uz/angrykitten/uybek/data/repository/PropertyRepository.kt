package uz.angrykitten.uybek.data.repository

import android.content.Context
import com.google.gson.Gson
import io.github.jan.supabase.postgrest.postgrest
import uz.angrykitten.uybek.data.SupabaseClientProvider
import uz.angrykitten.uybek.data.model.AppData
import uz.angrykitten.uybek.data.model.City
import uz.angrykitten.uybek.data.model.District
import uz.angrykitten.uybek.data.model.Property

class PropertyRepository(private val context: Context) {

    private val gson = Gson()
    private val client = SupabaseClientProvider.client
    private var appData: AppData? = null
    private var loadedFromSupabase = false

    private fun loadLocalData(): AppData {
        val json = context.assets.open("sample_data.json").bufferedReader().use { it.readText() }
        return gson.fromJson(json, AppData::class.java)
    }

    private fun currentData(): AppData {
        if (appData == null) {
            appData = loadLocalData()
        }
        return appData!!
    }

    suspend fun refreshFromSupabase(force: Boolean = false): Result<Unit> {
        if (!force && loadedFromSupabase && appData != null) {
            return Result.success(Unit)
        }
        return try {
            val cities = client.postgrest["cities"].select().decodeList<City>().sortedBy { it.id }
            val districts = client.postgrest["districts"].select().decodeList<District>().sortedBy { it.id }
            val properties = client.postgrest["properties"].select().decodeList<Property>()
            appData = AppData(
                cities = cities,
                districts = districts,
                properties = properties
            )
            loadedFromSupabase = true
            Result.success(Unit)
        } catch (e: Exception) {
            if (appData == null) {
                appData = loadLocalData()
            }
            loadedFromSupabase = false
            Result.failure(e)
        }
    }

    fun getCities(): List<City> = currentData().cities.sortedBy { it.id }

    fun getDistricts(cityId: Int? = null): List<District> {
        val all = currentData().districts.sortedBy { it.id }
        return if (cityId != null) all.filter { it.city_id == cityId } else all
    }

    fun getProperties(
        dealType: String? = null,
        propertyType: String? = null,
        cityId: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        bedrooms: Int? = null,
        query: String? = null,
        savedIds: Set<String> = emptySet(),
        onlySaved: Boolean = false
    ): List<Property> {
        var list = currentData().properties.filter { it.is_active }
        if (onlySaved) {
            list = list.filter { it.id in savedIds }
        }
        dealType?.let { dt -> list = list.filter { it.deal_type == dt } }
        propertyType?.let { pt -> list = list.filter { it.property_type == pt } }
        cityId?.let { cid -> list = list.filter { it.city_id == cid } }
        minPrice?.let { min -> list = list.filter { it.price >= min } }
        maxPrice?.let { max -> list = list.filter { it.price <= max } }
        bedrooms?.let { b -> list = list.filter { it.bedrooms >= b } }
        query?.let { q ->
            if (q.isNotBlank()) {
                val lower = q.lowercase()
                list = list.filter {
                    it.title.lowercase().contains(lower) ||
                    it.city_name.lowercase().contains(lower) ||
                    it.district_name.lowercase().contains(lower) ||
                    it.address.lowercase().contains(lower)
                }
            }
        }
        return list
    }

    fun getPropertyById(id: String): Property? =
        currentData().properties.find { it.id == id }

    suspend fun addProperty(property: Property): Result<Unit> {
        // Save locally first so the listing appears immediately regardless of network
        val data = currentData()
        appData = data.copy(properties = listOf(property) + data.properties)

        // Attempt background sync to Supabase – failures are silent to the user
        return try {
            client.postgrest["properties"].upsert(property)
            Result.success(Unit)
        } catch (e: Exception) {
            // Already saved locally above – report failure only to caller for logging
            Result.failure(e)
        }
    }

    suspend fun deleteProperty(id: String): Result<Unit> {
        return try {
            client.postgrest["properties"].delete {
                filter {
                    eq("id", id)
                }
            }
            refreshFromSupabase(force = true)
        } catch (e: Exception) {
            val data = currentData()
            appData = data.copy(properties = data.properties.filter { it.id != id })
            Result.failure(e)
        }
    }

    fun getUserProperties(userId: String): List<Property> =
        currentData().properties.filter { it.user_id == userId }
}
