package uz.angrykitten.pavo.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://injnswemzdxtrcduefvf.supabase.co",
        supabaseKey = "YOUR_SUPABASE_ANON_KEY"   // Dashboard → Settings → API → anon public
    ) {
        install(Postgrest)
        install(Storage)
    }
}
