package com.tamboo.productslist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamboo.productlist.R
import com.tamboo.productslist.uistate.ProductsListUiState
import com.tamboo.ui.components.ProductItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductsListScreen(
    viewModel: ProductsListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val uiState = state) {
            is ProductsListUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ProductsListUiState.Error -> {
                Text(
                    text = stringResource(R.string.error, uiState.message),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is ProductsListUiState.Success -> {
                if (uiState.products.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_product_available),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.products) { product ->
                            ProductItem(
                                product = product,
                                onFavoriteClick = { viewModel.toggleFavorite(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}
