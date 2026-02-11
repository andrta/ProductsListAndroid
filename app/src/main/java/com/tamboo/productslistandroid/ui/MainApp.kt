package com.tamboo.productslistandroid.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tamboo.productslist.presentation.ProductsListScreen
import com.tamboo.ui.navigation.Screen

@Composable
fun MainApp() {
    val navController = rememberNavController()

    // Le schermate definite nel modulo :core:ui
    val items = listOf(Screen.Products, Screen.Favorites, Screen.Profile)

    Scaffold(
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
            startDestination = Screen.Products.route, // La schermata iniziale
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. ROTTA PRODOTTI (Reale)
            composable(Screen.Products.route) {
                // Questa viene dal modulo :features:product-list
                ProductsListScreen()
            }

            // 2. ROTTA FAVORITI (Placeholder - La faremo nel prossimo branch)
            composable(Screen.Favorites.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Favorites Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }

            // 3. ROTTA PROFILO (Placeholder)
            composable(Screen.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile Coming Soon", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
