package uz.angrykitten.uybek.data.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class District(
    val id: Int,
    val city_id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class Property(
    val id: String,
    val user_id: String,
    val title: String,
    val description: String,
    val deal_type: String,      // "sale" | "rent"
    val property_type: String,  // "apartment" | "house" | "commercial" | "land"
    val city_id: Int,
    val city_name: String,
    val district_id: Int,
    val district_name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    val currency: String,       // "USD" | "UZS"
    val price_period: String?,  // "month" | "year" | null
    val area_m2: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val floor: Int,
    val total_floors: Int,
    val year_built: Int,
    val is_new_build: Boolean,
    val is_active: Boolean,
    val views_count: Int,
    val images: List<String>,
    val seller_name: String,
    val seller_phone: String,
    val seller_whatsapp: String,
    val seller_avatar: String
)

@Serializable
data class AppData(
    val cities: List<City>,
    val districts: List<District>,
    val properties: List<Property>
)
