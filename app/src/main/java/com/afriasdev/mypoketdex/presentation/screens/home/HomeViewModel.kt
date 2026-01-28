package com.afriasdev.mypoketdex.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.usecase.GetPokemonListUseCase
import com.afriasdev.mypoketdex.domain.usecase.SearchPokemonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPokemonListUseCase: GetPokemonListUseCase,
    private val searchPokemonUseCase: SearchPokemonUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _pokemonPagingFlow = MutableStateFlow<PagingData<Pokemon>>(PagingData.empty())
    val pokemonPagingFlow: StateFlow<PagingData<Pokemon>> = _pokemonPagingFlow.asStateFlow()

    private val _selectedTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedTypes: StateFlow<Set<String>> = _selectedTypes.asStateFlow()

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    init {
        loadPokemonList()
    }

    private fun loadPokemonList() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading

                getPokemonListUseCase()
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .map { pagingData ->
                        // Aplicar filtro de tipos si hay tipos seleccionados
                        if (_selectedTypes.value.isEmpty()) {
                            pagingData
                        } else {
                            pagingData.filter { pokemon ->
                                pokemon.types.any { it in _selectedTypes.value }
                            }
                        }
                    }
                    .collect { pagingData ->
                        _pokemonPagingFlow.value = pagingData
                        _uiState.value = HomeUiState.Success
                    }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Error desconocido al cargar Pokémon"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            loadPokemonList()
        }
    }

    fun onSearch() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) {
            loadPokemonList()
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Searching

                val results = searchPokemonUseCase(query)

                if (results.isEmpty()) {
                    _uiState.value = HomeUiState.EmptySearch(query)
                } else {
                    // Aplicar filtros de tipo a los resultados de búsqueda
                    val filteredResults = if (_selectedTypes.value.isEmpty()) {
                        results
                    } else {
                        results.filter { pokemon ->
                            pokemon.types.any { it in _selectedTypes.value }
                        }
                    }

                    _pokemonPagingFlow.value = PagingData.from(filteredResults)
                    _uiState.value = HomeUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Error al buscar Pokémon"
                )
            }
        }
    }

    fun onClearSearch() {
        _searchQuery.value = ""
        loadPokemonList()
    }

    fun toggleFilterSheet() {
        _showFilterSheet.value = !_showFilterSheet.value
    }

    fun dismissFilterSheet() {
        _showFilterSheet.value = false
    }

    fun toggleTypeFilter(type: String) {
        _selectedTypes.value = if (type in _selectedTypes.value) {
            _selectedTypes.value - type
        } else {
            _selectedTypes.value + type
        }

        // Recargar la lista con los nuevos filtros
        loadPokemonList()
    }

    fun clearFilters() {
        _selectedTypes.value = emptySet()
        loadPokemonList()
    }

    fun retry() {
        loadPokemonList()
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Searching : HomeUiState
    data object Success : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class EmptySearch(val query: String) : HomeUiState
}