package com.tamboo.profile.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamboo.domain.usecase.GetFavoriteProductsUseCase
import com.tamboo.profile.domain.usecase.GetUserProfileUseCase
import com.tamboo.profile.presentation.uistate.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            combine(
                flow { emit(getUserProfileUseCase()) },
                getFavoriteProductsUseCase()
            ) { userResult, favorites ->
                userResult.onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(
                        user = user,
                        favoritesCount = favorites.size
                    )
                }.onFailure {
                    _uiState.value = ProfileUiState.Error(it.message ?: "Unknown error")
                }
            }.collect()
        }
    }
}
