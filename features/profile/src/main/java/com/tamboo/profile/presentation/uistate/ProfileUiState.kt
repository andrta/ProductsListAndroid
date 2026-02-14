package com.tamboo.profile.presentation.uistate

import com.tamboo.profile.domain.model.User

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val user: User, val favoritesCount: Int) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
