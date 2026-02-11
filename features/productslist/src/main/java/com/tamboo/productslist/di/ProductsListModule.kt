package com.tamboo.productslist.di

import com.tamboo.productslist.presentation.ProductsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val productsListModule = module {
    viewModelOf(::ProductsListViewModel)
}
