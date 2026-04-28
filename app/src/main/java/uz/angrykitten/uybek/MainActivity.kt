package uz.angrykitten.uybek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.angrykitten.uybek.ui.navigation.AppNavGraph
import uz.angrykitten.uybek.ui.theme.UybekTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UybekTheme {
                AppNavGraph()
            }
        }
    }
}