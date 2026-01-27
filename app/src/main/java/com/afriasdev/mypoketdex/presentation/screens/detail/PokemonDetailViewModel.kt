package com.afriasdev.mypoketdex.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.usecase.GetPokemonDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class PokemonDetailViewModel(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadPokemonDetail(pokemonId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = DetailUiState.Loading

                val pokemon = getPokemonDetailUseCase(pokemonId)

                if (pokemon != null) {
                    _uiState.value = DetailUiState.Success(pokemon)
                } else {
                    _uiState.value = DetailUiState.Error("Pokémon no encontrado")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading pokemon detail: $pokemonId")
                _uiState.value = DetailUiState.Error(
                    e.message ?: "Error al cargar detalles del Pokémon"
                )
            }
        }
    }

    fun retry(pokemonId: Int) {
        loadPokemonDetail(pokemonId)
    }

}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val pokemon: Pokemon) : DetailUiState
    data class Error(val message: String) : DetailUiState
}