package uz.angrykitten.uybek.data.repository

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import uz.angrykitten.uybek.data.SupabaseClientProvider

/**
 * Data model that mirrors the `users` table in Supabase/PostgreSQL.
 *
 * SQL to create the table (run once in Supabase SQL editor):
 *
 * create table if not exists public.users (
 *   id text primary key,
 *   name text,
 *   email text,
 *   phone text,
 *   avatar_url text,
 *   created_at timestamptz default now()
 * );
 * -- Enable Row Level Security and allow inserts/reads
 * alter table public.users enable row level security;
 * create policy "Users can upsert own row" on public.users
 *   for all using (true) with check (true);
 */
@Serializable
data class SupabaseUser(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatar_url: String? = null
)

/**
 * Repository for syncing user profiles to Supabase PostgreSQL.
 */
class SupabaseRepository {

    private val client = SupabaseClientProvider.client

    /** Upserts (insert or update) a user profile row in the `users` table. */
    suspend fun upsertUser(user: SupabaseUser): Result<Unit> {
        return try {
            client.postgrest["users"].upsert(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Fetches a user profile by Firebase UID. */
    suspend fun getUser(uid: String): Result<SupabaseUser?> {
        return try {
            val result = client.postgrest["users"]
                .select {
                    filter { eq("id", uid) }
                }
                .decodeSingleOrNull<SupabaseUser>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
