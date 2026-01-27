package com.afriasdev.mypoketdex.domain.repository

import androidx.paging.PagingData
import com.afriasdev.mypoketdex.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(): Flow<PagingData<Pokemon>>
    suspend fun getPokemonById(id: Int): Pokemon?
    suspend fun searchPokemon(query: String): List<Pokemon>
}