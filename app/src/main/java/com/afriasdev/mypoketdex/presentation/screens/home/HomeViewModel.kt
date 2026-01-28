package com.afriasdev.mypoketdex.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.usecase.GetPokemonListUseCase
import com.afriasdev.mypoketdex.domain.usecase.SearchPokemonUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val getPokemonListUseCase: GetPokemonListUseCase,
    private val searchPokemonUseCase: SearchPokemonUseCase
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedTypes: StateFlow<Set<String>> = _selectedTypes.asStateFlow()

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Pokemon>?>(null)
    val searchResults: StateFlow<List<Pokemon>?> = _searchResults.asStateFlow()

    // Flow de Pokémon con filtros aplicados
    val pokemonPagingFlow: StateFlow<PagingData<Pokemon>> = combine(
        getPokemonListUseCase()
            .cachedIn(viewModelScope),
        _selectedTypes
    ) { pagingData, selectedTypes ->
        if (selectedTypes.isEmpty()) {
            pagingData
        } else {
            pagingData.filter { pokemon ->
                pokemon.types.any { it in selectedTypes }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Si el query está vacío, limpiar resultados de búsqueda
        if (query.isEmpty()) {
            _searchResults.value = null
        }
    }

    fun onSearch() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) {
            _searchResults.value = null
            return
        }

        viewModelScope.launch {
            try {
                _isSearching.value = true
                val results = searchPokemonUseCase(query)

                // Aplicar filtros de tipo a los resultados de búsqueda
                val filteredResults = if (_selectedTypes.value.isEmpty()) {
                    results
                } else {
                    results.filter { pokemon ->
                        pokemon.types.any { it in _selectedTypes.value }
                    }
                }

                _searchResults.value = filteredResults
                Timber.d("Search results: ${filteredResults.size}")
            } catch (e: Exception) {
                Timber.e(e, "Error searching pokemon")
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun onClearSearch() {
        _searchQuery.value = ""
        _searchResults.value = null
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

        // Si hay búsqueda activa, re-buscar con los nuevos filtros
        if (_searchResults.value != null) {
            onSearch()
        }
    }

    fun clearFilters() {
        _selectedTypes.value = emptySet()

        // Si hay búsqueda activa, re-buscar sin filtros
        if (_searchResults.value != null) {
            onSearch()
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Searching : HomeUiState
    data object Success : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class EmptySearch(val query: String) : HomeUiState
}