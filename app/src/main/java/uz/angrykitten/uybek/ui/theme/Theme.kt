package uz.angrykitten.uybek.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Brand,
    onPrimary = Color.White,
    primaryContainer = BgCard,
    onPrimaryContainer = BrandDark,
    secondary = AccentRent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = AccentSale,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    error = ColorError,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = BgLight,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = Divider,
    scrim = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandLight,
    onPrimary = BrandDark,
    primaryContainer = BgDarkCard,
    onPrimaryContainer = BrandLight,
    secondary = AccentRent,
    onSecondary = Color(0xFF3F2817),
    secondaryContainer = Color(0xFF5A4032),
    onSecondaryContainer = Color(0xFFFFDCC4),
    tertiary = AccentSale,
    onTertiary = Color(0xFF003D2E),
    tertiaryContainer = Color(0xFF0F5F44),
    onTertiaryContainer = Color(0xFF7FFBDE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = BgDark,
    onBackground = TextDarkPrimary,
    surface = BgDarkSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = BgDarkCard,
    onSurfaceVariant = TextDarkSecondary,
    outline = TextDarkHint,
    outlineVariant = DividerDark,
    scrim = Color.Black
)

@Composable
fun UybekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
