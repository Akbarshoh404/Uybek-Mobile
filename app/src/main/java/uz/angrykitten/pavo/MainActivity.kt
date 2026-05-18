package uz.angrykitten.pavo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import uz.angrykitten.pavo.ui.navigation.AppNavGraph
import uz.angrykitten.pavo.ui.localization.AppLanguage
import uz.angrykitten.pavo.ui.localization.LocalAppLanguage
import uz.angrykitten.pavo.ui.theme.PavoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by rememberSaveable { mutableStateOf(systemDark) }
            var languageName by rememberSaveable { mutableStateOf(AppLanguage.UZ.name) }
            val appLanguage = remember(languageName) { AppLanguage.valueOf(languageName) }
            val view = LocalView.current

            PavoTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalAppLanguage provides appLanguage) {
                    // Fix status bar icon colors based on theme
                    SideEffect {
                        val window = (view.context as ComponentActivity).window
                        window.statusBarColor = Color.Transparent.toArgb()
                        WindowCompat.getInsetsController(window, view)
                            .isAppearanceLightStatusBars = !darkTheme
                        WindowCompat.getInsetsController(window, view)
                            .isAppearanceLightNavigationBars = !darkTheme
                    }
                    AppNavGraph(
                        onToggleTheme = { darkTheme = !darkTheme },
                        currentLanguage = appLanguage,
                        onChangeLanguage = { languageName = it.name }
                    )
                }
            }
        }
    }
}
