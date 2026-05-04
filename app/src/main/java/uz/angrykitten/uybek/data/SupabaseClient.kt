package uz.angrykitten.uybek.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://owmyeempolkojucuhgvm.supabase.co",
        supabaseKey = "sb_publishable_3UvDykZjVuHLz2jBA04M-Q_WjPjcr0-"
    ) {
        install(Postgrest)
    }
}
