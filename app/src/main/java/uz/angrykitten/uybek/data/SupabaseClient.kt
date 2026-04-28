package uz.angrykitten.uybek.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://owmyeempolkojucuhgvm.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im93bXllZW1wb2xrb2p1Y3VoZ3ZtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MjAwMDAwMDAwMH0.placeholder"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
