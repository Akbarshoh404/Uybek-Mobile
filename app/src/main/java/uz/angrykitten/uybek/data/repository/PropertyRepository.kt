package uz.angrykitten.uybek.data.repository

import android.content.Context
import com.google.gson.Gson
import uz.angrykitten.uybek.data.model.AppData
import uz.angrykitten.uybek.data.model.City
import uz.angrykitten.uybek.data.model.District
import uz.angrykitten.uybek.data.model.Property

class PropertyRepository(private val context: Context) {

    private val gson = Gson()
    private var appData: AppData? = null

    private fun loadData(): AppData {
        if (appData != null) return appData!!
        val json = context.assets.open("sample_data.json").bufferedReader().use { it.readText() }
        appData = gson.fromJson(json, AppData::class.java)
        return appData!!
    }

    fun getCities(): List<City> = loadData().cities

    fun getDistricts(cityId: Int? = null): List<District> {
        val all = loadData().districts
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
        var list = loadData().properties.filter { it.is_active }
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
        loadData().properties.find { it.id == id }

    fun addProperty(property: Property) {
        val data = loadData()
        appData = data.copy(properties = data.properties + property)
    }

    fun deleteProperty(id: String) {
        val data = loadData()
        appData = data.copy(properties = data.properties.filter { it.id != id })
    }

    fun getUserProperties(userId: String): List<Property> =
        loadData().properties.filter { it.user_id == userId }
}
