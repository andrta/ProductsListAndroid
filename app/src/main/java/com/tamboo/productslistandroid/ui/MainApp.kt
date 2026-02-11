package com.tamboo.productslistandroid.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tamboo.favorites.presentation.FavoritesScreen
import com.tamboo.productslist.presentation.ProductsListScreen
import com.tamboo.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()

    val items = listOf(Screen.Products, Screen.Favorites, Screen.Profile)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screenTitle = when (currentRoute) {
        Screen.Products.route -> "Fake Store Shop"
        Screen.Favorites.route -> "Favourites"
        Screen.Profile.route -> "My Profile"
        else -> "Fake Store"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        screenTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        // Logica per evidenziare l'icona attiva
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Evita di accumulare schermate nello stack quando clicchi avanti e indietro
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Evita di aprire copie multiple della stessa schermata se ci clicchi sopra ancora
                                launchSingleTop = true
                                // Ripristina lo stato (es. scroll) quando torni su questa tab
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Products.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(400)) +
                        slideInVertically(initialOffsetY = { it / 40 })
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {

            composable(Screen.Products.route) {
                ProductsListScreen()
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen()
            }

            composable(Screen.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
