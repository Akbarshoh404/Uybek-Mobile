package uz.angrykitten.uybek.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import uz.angrykitten.uybek.ui.screens.*
import uz.angrykitten.uybek.ui.theme.Brand
import uz.angrykitten.uybek.ui.viewmodel.AppViewModel

@Composable
fun AppNavGraph(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavRoutes = setOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.PostListing.route,
        Screen.Saved.route,
        Screen.Profile.route
    )
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(tween(300)) { it },
                exit = slideOutVertically(tween(300)) { it }
            ) {
                ModernNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(tween(400)) { it / 2 } + fadeIn(tween(400))
            },
            exitTransition = {
                slideOutHorizontally(tween(400)) { -it / 2 } + fadeOut(tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(tween(400)) { -it / 2 } + fadeIn(tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(tween(400)) { it / 2 } + fadeOut(tween(400))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.Search.route) {
                SearchScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.PostListing.route) {
                PostListingScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.Saved.route) {
                SavedScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.PropertyDetail.route) { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: return@composable
                PropertyDetailScreen(
                    propertyId = propertyId,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.MyListings.route) {
                MyListingsScreen(viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun ModernNavigationBar(
    navController: androidx.navigation.NavController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.screen.route
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) Brand.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Brand,
                    selectedTextColor = Brand,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                alwaysShowLabel = true
            )
        }
    }
}
