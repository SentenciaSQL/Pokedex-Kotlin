package com.afriasdev.mypoketdex.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.afriasdev.mypoketdex.data.local.database.PokedexDatabase
import com.afriasdev.mypoketdex.data.local.entity.toDomain
import com.afriasdev.mypoketdex.data.local.entity.toEntity
import com.afriasdev.mypoketdex.data.mapper.PokemonMapper
import com.afriasdev.mypoketdex.data.remote.api.PokeApiService
import com.afriasdev.mypoketdex.data.remote.mediator.PokemonRemoteMediator
import com.afriasdev.mypoketdex.data.remote.paging.PokemonPagingSource
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class PokemonRepositoryImpl(private val api: PokeApiService, private val database: PokedexDatabase): PokemonRepository {

    private val pokemonDao = database.pokemonDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getPokemonList(): Flow<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                pageSize = PokeApiService.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            remoteMediator = PokemonRemoteMediator(
                api = api,
                database = database
            ),
            pagingSourceFactory = { pokemonDao.getAllPokemon() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getPokemonById(id: Int): Pokemon? {
        return try {
            // Primero intentar obtener de la base de datos local
            val localPokemon = pokemonDao.getPokemonById(id)
            if (localPokemon != null) {
                Timber.d("Pokemon $id found in local database")
                return localPokemon.toDomain()
            }

            // Si no está en local, obtener de la API
            Timber.d("Pokemon $id not in local database, fetching from API")
            val response = api.getPokemonDetail(id)
            val pokemon = PokemonMapper.toDomain(response)

            // Guardar en la base de datos para futuras consultas
            pokemonDao.insertPokemon(pokemon.toEntity())

            pokemon
        } catch (e: Exception) {
            Timber.e(e, "Error getting pokemon by id: $id")
            null
        }
    }

    override suspend fun searchPokemon(query: String): List<Pokemon> {
        return try {
            val idQuery = query.toIntOrNull()

            // Primero buscar en la base de datos local
            val localResults = pokemonDao.searchPokemon(query.lowercase(), idQuery)

            if (localResults.isNotEmpty()) {
                Timber.d("Found ${localResults.size} results in local database")
                return localResults.map { it.toDomain() }
            }

            // Si no hay resultados locales, buscar en la API
            Timber.d("No local results, searching API")
            val pokemon = try {
                val response = api.getPokemonByName(query.lowercase())
                listOf(PokemonMapper.toDomain(response))
            } catch (e: Exception) {
                if (idQuery != null && idQuery > 0) {
                    val response = api.getPokemonDetail(idQuery)
                    listOf(PokemonMapper.toDomain(response))
                } else {
                    emptyList()
                }
            }

            // Guardar resultados en la base de datos
            pokemon.forEach { pokemonDao.insertPokemon(it.toEntity()) }

            pokemon
        } catch (e: Exception) {
            Timber.e(e, "Error searching pokemon: $query")
            emptyList()
        }
    }
}