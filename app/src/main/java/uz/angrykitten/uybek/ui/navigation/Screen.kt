package uz.angrykitten.uybek.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object PostListing : Screen("post_listing")
    data object Saved : Screen("saved")
    data object Profile : Screen("profile")
    data object PropertyDetail : Screen("property_detail/{propertyId}") {
        fun createRoute(id: String) = "property_detail/$id"
    }
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object MyListings : Screen("my_listings")
    data object Splash : Screen("splash")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Asosiy", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Search, "Qidiruv", Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(Screen.PostListing, "E'lon", Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
    BottomNavItem(Screen.Saved, "Saqlangan", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomNavItem(Screen.Profile, "Profil", Icons.Filled.Person, Icons.Outlined.Person),
)
