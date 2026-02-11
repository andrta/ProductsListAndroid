package com.tamboo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Products : Screen("products", "Home", Icons.Default.Home)
    data object Favorites : Screen("favorites", "Favoriti", Icons.Default.Favorite)
    data object Profile : Screen("profile", "Profilo", Icons.Default.Person)
}
