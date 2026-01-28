package com.afriasdev.mypoketdex.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.afriasdev.mypoketdex.data.local.database.PokedexDatabase
import com.afriasdev.mypoketdex.data.local.entity.PokemonEntity
import com.afriasdev.mypoketdex.data.local.entity.PokemonRemoteKeys
import com.afriasdev.mypoketdex.data.local.entity.toEntity
import com.afriasdev.mypoketdex.data.mapper.PokemonMapper
import com.afriasdev.mypoketdex.data.remote.api.PokeApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokeApiService,
    private val database: PokedexDatabase
) : RemoteMediator<Int, PokemonEntity>()  {

    private val pokemonDao = database.pokemonDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        // Verificar si tenemos datos en caché
        val cacheTimeout = System.currentTimeMillis() - (1000 * 60 * 60 * 24) // 24 horas

        return if (pokemonDao.count() > 0) {
            // Si tenemos datos, no necesitamos refrescar inmediatamente
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Si no hay datos, hacer refresh inicial
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Timber.d("LoadType.REFRESH")
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    Timber.d("LoadType.PREPEND - returning early")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    if (nextKey == null) {
                        Timber.d("LoadType.APPEND - no next key, end of pagination")
                        return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    Timber.d("LoadType.APPEND - next key: $nextKey")
                    nextKey
                }
            }

            val offset = page * PokeApiService.PAGE_SIZE
            Timber.d("Loading page: $page, offset: $offset")

            // Obtener lista básica
            val response = api.getPokemonList(
                limit = PokeApiService.PAGE_SIZE,
                offset = offset
            )

            val endOfPaginationReached = response.next == null

            // OPTIMIZACIÓN: Cargar detalles en paralelo usando coroutines
            val pokemonList = coroutineScope {
                response.results.map { pokemonDto ->
                    async {
                        try {
                            val detail = api.getPokemonDetail(pokemonDto.id)
                            PokemonMapper.toDomain(detail).toEntity()
                        } catch (e: Exception) {
                            Timber.e(e, "Error loading details for ${pokemonDto.name}")
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Timber.d("Clearing database for refresh")
                    remoteKeysDao.clearAll()
                    pokemonDao.clearAll()
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = pokemonList.map { pokemon ->
                    PokemonRemoteKeys(
                        pokemonId = pokemon.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                remoteKeysDao.insertAll(keys)
                pokemonDao.insertAllPokemon(pokemonList)

                Timber.d("Saved ${pokemonList.size} pokemon to database")
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            Timber.e(e, "Error in RemoteMediator")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, PokemonEntity>
    ): PokemonRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { pokemon ->
            remoteKeysDao.getRemoteKeys(pokemon.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PokemonEntity>
    ): PokemonRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { pokemonId ->
                remoteKeysDao.getRemoteKeys(pokemonId)
            }
        }
    }
}