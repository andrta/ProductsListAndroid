package com.tamboo.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.tamboo.ui.R

sealed class Screen(
    val route: String,
    @param:StringRes val titleRes: Int,
    val icon: ImageVector
) {
    data object Products : Screen("products", R.string.home, Icons.Default.Home)
    data object Favorites : Screen("favorites", R.string.favorites, Icons.Default.Favorite)
    data object Profile : Screen("profile", R.string.profile, Icons.Default.Person)
}
