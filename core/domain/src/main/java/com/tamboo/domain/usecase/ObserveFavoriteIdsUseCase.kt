package com.tamboo.domain.usecase

import com.tamboo.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteIdsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Set<Int>> {
        return repository.observeFavoriteIds()
    }
}
