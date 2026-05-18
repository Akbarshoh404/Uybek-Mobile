package uz.angrykitten.pavo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalDarkTheme = compositionLocalOf { true }

private val DarkColorScheme = darkColorScheme(
    primary = AccentSky,
    onPrimary = BgDark,
    primaryContainer = BrandDark,
    onPrimaryContainer = TextDarkPrimary,
    secondary = AccentGold,
    onSecondary = BgDark,
    tertiary = AccentRose,
    onTertiary = BgDark,
    background = BgDark,
    onBackground = TextDarkPrimary,
    surface = BgDarkSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = BgDarkCard,
    onSurfaceVariant = TextDarkSecondary,
    outline = DividerDark,
    outlineVariant = BrandDark,
    error = ColorError
)

private val LightColorScheme = lightColorScheme(
    primary = Brand,
    onPrimary = Color.White,
    primaryContainer = BrandLight,
    onPrimaryContainer = BrandDark,
    secondary = AccentSale,
    onSecondary = Color.White,
    tertiary = AccentRent,
    onTertiary = Color.White,
    background = BgLight,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = AccentLight,
    onSurfaceVariant = TextSecondary,
    outline = Divider,
    outlineVariant = Color(0xFFF0EBE2),
    error = ColorError
)

@Composable
fun PavoTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    androidx.compose.runtime.CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
