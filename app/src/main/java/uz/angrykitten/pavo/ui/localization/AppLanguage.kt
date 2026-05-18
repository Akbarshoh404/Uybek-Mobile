package uz.angrykitten.pavo.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage {
    UZ,
    EN,
    RU
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.UZ }

@Composable
fun tr(uz: String, en: String, ru: String): String {
    return when (LocalAppLanguage.current) {
        AppLanguage.UZ -> uz
        AppLanguage.EN -> en
        AppLanguage.RU -> ru
    }
}
