package com.afriasdev.mypoketdex.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.afriasdev.mypoketdex.data.remote.api.PokeApiService
import com.afriasdev.mypoketdex.domain.model.Pokemon
import timber.log.Timber
import com.afriasdev.mypoketdex.data.mapper.PokemonMapper

class PokemonPagingSource(private val api: PokeApiService): PagingSource<Int, Pokemon>() {
    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        return try {
            val page = params.key ?: 0
            val offset = page * PokeApiService.PAGE_SIZE

            Timber.d("Loading page: $page, offset: $offset")

            val response = api.getPokemonList(
                limit = PokeApiService.PAGE_SIZE,
                offset = offset
            )

            // Convertir DTOs básicos a Pokemon con tipos
            val pokemonList = response.results.mapNotNull { pokemonDto ->
                try {
                    // Obtener detalles para cada pokemon para tener los tipos
                    val detail = api.getPokemonDetail(pokemonDto.id)
                    PokemonMapper.toDomain(detail)
                } catch (e: Exception) {
                    Timber.e(e, "Error loading details for ${pokemonDto.name}")
                    // Si falla, usar datos básicos sin tipos
                    PokemonMapper.toDomain(pokemonDto)
                }
            }

            Timber.d("Loaded ${pokemonList.size} pokemon")

            LoadResult.Page(
                data = pokemonList,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.next != null) page + 1 else null
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading pokemon page")
            LoadResult.Error(e)
        }
    }
}