package com.afriasdev.mypoketdex.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.afriasdev.mypoketdex.R
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.presentation.components.ErrorMessage
import com.afriasdev.mypoketdex.presentation.components.LoadingIndicator
import com.afriasdev.mypoketdex.presentation.components.PokedexSearchBar
import com.afriasdev.mypoketdex.presentation.components.PokemonCard
import com.afriasdev.mypoketdex.presentation.screens.home.components.FilterBottomSheet
import com.afriasdev.mypoketdex.ui.theme.PokeRed
import com.afriasdev.mypoketdex.ui.theme.PokeRedDark
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(onPokemonClick: (Int) -> Unit, viewModel: HomeViewModel = koinViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTypes by viewModel.selectedTypes.collectAsState()
    val showFilterSheet by viewModel.showFilterSheet.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val pokemonPagingItems = viewModel.pokemonPagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            HomeTopBar(
                selectedFiltersCount = selectedTypes.size,
                onFilterClick = viewModel::toggleFilterSheet
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            PokedexSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                onClear = viewModel::onClearSearch,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Chips de filtros activos
            if (selectedTypes.isNotEmpty()) {
                ActiveFiltersRow(
                    selectedTypes = selectedTypes,
                    onClearFilters = viewModel::clearFilters,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Contenido basado en el estado
            when {
                // Búsqueda en progreso
                isSearching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator(message = "Buscando Pokémon...")
                    }
                }

                // Resultados de búsqueda disponibles
                searchResults != null -> {
                    if (searchResults!!.isEmpty()) {
                        EmptySearchState(query = searchQuery)
                    } else {
                        SearchResultsGrid(
                            results = searchResults!!,
                            onPokemonClick = onPokemonClick
                        )
                    }
                }

                // Lista normal con paginación
                else -> {
                    PokemonGrid(
                        pokemonPagingItems = pokemonPagingItems,
                        onPokemonClick = onPokemonClick
                    )
                }
            }
        }
    }

    // Bottom Sheet de filtros
    if (showFilterSheet) {
        FilterBottomSheet(
            selectedTypes = selectedTypes,
            onTypeToggle = viewModel::toggleTypeFilter,
            onClearFilters = {
                viewModel.clearFilters()
                viewModel.dismissFilterSheet()
            },
            onDismiss = viewModel::dismissFilterSheet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    selectedFiltersCount: Int,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CatchingPokemon,
                    contentDescription = stringResource(R.string.pokedex),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.pokedex).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (selectedFiltersCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Text(
                                text = selectedFiltersCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PokeRed
        ),
        modifier = Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(PokeRed, PokeRedDark)
            )
        )
    )
}

@Composable
fun ActiveFiltersRow(
    selectedTypes: Set<String>,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${selectedTypes.size} filtro(s) activo(s)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TextButton(onClick = onClearFilters) {
            Text("Limpiar")
        }
    }
}

@Composable
fun SearchResultsGrid(results: List<Pokemon>, onPokemonClick: (Int) -> Unit,  modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                text = "${results.size} resultado(s) encontrado(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = results,
            key = { pokemon -> pokemon.id }
        ) { pokemon ->
            PokemonCard(
                pokemon = pokemon,
                onClick = { onPokemonClick(pokemon.id) }
            )
        }
    }
}

@Composable
fun PokemonGrid(
    pokemonPagingItems: LazyPagingItems<Pokemon>,
    onPokemonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = pokemonPagingItems.itemCount,
                key = { index -> pokemonPagingItems.peek(index)?.id ?: index }
            ) { index ->
                pokemonPagingItems[index]?.let { pokemon ->
                    PokemonCard(
                        pokemon = pokemon,
                        onClick = { onPokemonClick(pokemon.id) }
                    )
                }
            }

            // Loading footer
            when (pokemonPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        ErrorMessage(
                            message = "Error al cargar más Pokémon",
                            onRetry = { pokemonPagingItems.retry() }
                        )
                    }
                }

                else -> {}
            }
        }

        // Loading inicial
        if (pokemonPagingItems.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Error en refresh
        if (pokemonPagingItems.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = "Error al cargar Pokémon",
                    onRetry = { pokemonPagingItems.retry() }
                )
            }
        }
    }
}

@Composable
fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${stringResource(R.string.pokemom_not_found)} \"$query\"",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.try_to_find_by_nymber_or_id),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}