package uz.angrykitten.pavo.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.angrykitten.pavo.data.SupabaseClientProvider
import uz.angrykitten.pavo.data.model.AppData
import uz.angrykitten.pavo.data.model.City
import uz.angrykitten.pavo.data.model.District
import uz.angrykitten.pavo.data.model.Animal
import java.util.UUID

class AnimalRepository(private val context: Context) {

    private val client = SupabaseClientProvider.client
    private var appData: AppData? = null
    private var loadedFromSupabase = false

    /** Returns cached data or an empty placeholder — never crashes. */
    private fun currentData(): AppData =
        appData ?: AppData(cities = emptyList(), districts = emptyList(), animals = emptyList())

    suspend fun refreshFromSupabase(force: Boolean = false): Result<Unit> {
        if (!force && loadedFromSupabase && appData != null) {
            return Result.success(Unit)
        }
        return try {
            val cities = client.postgrest["cities"].select().decodeList<City>().sortedBy { it.id }
            val districts = client.postgrest["districts"].select().decodeList<District>().sortedBy { it.id }
            val animals = client.postgrest["animals"].select().decodeList<Animal>()
            appData = AppData(
                cities = cities,
                districts = districts,
                animals = animals
            )
            loadedFromSupabase = true
            Result.success(Unit)
        } catch (e: Exception) {
            if (appData == null) {
                appData = AppData(cities = emptyList(), districts = emptyList(), animals = emptyList())
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

    fun getAnimals(
        listingType: String? = null,
        animalType: String? = null,
        cityId: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        query: String? = null,
        savedIds: Set<String> = emptySet(),
        onlySaved: Boolean = false
    ): List<Animal> {
        var list = currentData().animals.filter { it.is_active }
        if (onlySaved) {
            list = list.filter { it.id in savedIds }
        }
        listingType?.let { lt -> list = list.filter { it.listing_type == lt } }
        animalType?.let { at -> list = list.filter { it.animal_type == at } }
        cityId?.let { cid -> list = list.filter { it.city_id == cid } }
        minPrice?.let { min -> list = list.filter { it.price >= min } }
        maxPrice?.let { max -> list = list.filter { it.price <= max } }
        query?.let { q ->
            if (q.isNotBlank()) {
                val lower = q.lowercase()
                list = list.filter {
                    it.title.lowercase().contains(lower) ||
                    it.breed.lowercase().contains(lower) ||
                    it.city_name.lowercase().contains(lower) ||
                    it.district_name.lowercase().contains(lower) ||
                    it.address.lowercase().contains(lower)
                }
            }
        }
        return list
    }

    fun getAnimalById(id: String): Animal? =
        currentData().animals.find { it.id == id }

    suspend fun addAnimal(animal: Animal): Result<Unit> {
        val data = currentData()
        appData = data.copy(animals = listOf(animal) + data.animals)

        return try {
            client.postgrest["animals"].upsert(animal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnimal(id: String): Result<Unit> {
        return try {
            client.postgrest["animals"].delete {
                filter {
                    eq("id", id)
                }
            }
            refreshFromSupabase(force = true)
        } catch (e: Exception) {
            val data = currentData()
            appData = data.copy(animals = data.animals.filter { it.id != id })
            Result.failure(e)
        }
    }

    fun getUserAnimals(userId: String): List<Animal> =
        currentData().animals.filter { it.user_id == userId }

    suspend fun uploadImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalArgumentException("Could not read image bytes")

            val mimeType = context.contentResolver.getType(uri)
                ?: MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()))
                ?: "image/jpeg"
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
            val objectPath = "uploads/${UUID.randomUUID()}.$ext"

            val bucket = client.storage["Animals"]
            bucket.upload(objectPath, bytes) {
                upsert = false
                contentType = io.ktor.http.ContentType.parse(mimeType)
            }

            val publicUrl = bucket.publicUrl(objectPath)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
