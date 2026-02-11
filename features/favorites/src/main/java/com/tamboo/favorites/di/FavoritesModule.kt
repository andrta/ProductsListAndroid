package com.tamboo.favorites.di

import com.tamboo.favorites.presentation.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val favoritesModule = module {
    viewModelOf(::FavoritesViewModel)
}
