package com.afriasdev.mypoketdex.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.presentation.components.ErrorMessage
import com.afriasdev.mypoketdex.presentation.components.InfoChip
import com.afriasdev.mypoketdex.presentation.components.LoadingIndicator
import com.afriasdev.mypoketdex.presentation.components.StatBar
import com.afriasdev.mypoketdex.ui.theme.getTypeColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    viewModel: PokemonDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetail(pokemonId)
    }

    when (val state = uiState) {
        is DetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(message = "Cargando detalles...")
            }
        }

        is DetailUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.retry(pokemonId) }
                )
            }
        }

        is DetailUiState.Success -> {
            PokemonDetailContent(
                pokemon = state.pokemon,
                onBackClick = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailContent(
    pokemon: Pokemon,
    onBackClick: () -> Unit
) {
    val dominantColor = if (pokemon.types.isNotEmpty()) {
        getTypeColor(pokemon.types.first())
    } else {
        MaterialTheme.colorScheme.primary
    }

    val secondaryColor = if (pokemon.types.size > 1) {
        getTypeColor(pokemon.types[1])
    } else {
        dominantColor.copy(alpha = 0.7f)
    }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(dominantColor, secondaryColor)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top Bar transparente
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Header con imagen y nombre
            PokemonHeader(pokemon = pokemon)

            // Card de información
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Tipos
                    PokemonTypes(types = pokemon.types)

                    Spacer(modifier = Modifier.height(24.dp))

                    // About section
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = dominantColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info chips
                    PokemonInfoSection(pokemon = pokemon)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats section
                    Text(
                        text = "Base Stats",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = dominantColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats
                    PokemonStats(
                        stats = pokemon.stats,
                        dominantColor = dominantColor
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun PokemonHeader(pokemon: Pokemon) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Número
        Text(
            text = "#${pokemon.id.toString().padStart(3, '0')}",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre
        Text(
            text = pokemon.name.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Imagen del Pokémon
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.name,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun PokemonTypes(types: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        types.forEach { type ->
            Surface(
                modifier = Modifier.padding(horizontal = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = getTypeColor(type)
            ) {
                Text(
                    text = type.uppercase(),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PokemonInfoSection(pokemon: Pokemon) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoChip(
            label = "Weight",
            value = "${pokemon.weightInKg} kg",
            icon = "⚖️",
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        InfoChip(
            label = "Height",
            value = "${pokemon.heightInMeters} m",
            icon = "📏",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PokemonStats(
    stats: List<com.afriasdev.mypoketdex.domain.model.Stat>,
    dominantColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            StatBar(
                statName = stat.displayName,
                statValue = stat.value,
                color = dominantColor
            )
        }

        // Total stats
        if (stats.isNotEmpty()) {
//            Divider(modifier = Modifier.padding(vertical = 8.dp))
//            VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            val totalStats = stats.sumOf { it.value }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = totalStats.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = dominantColor
                )
            }
        }
    }
}