package com.afriasdev.mypoketdex.domain.usecase

import androidx.paging.PagingData
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class GetPokemonListUseCase(private val repository: PokemonRepository) {
    operator fun invoke(): Flow<PagingData<Pokemon>> {
        return repository.getPokemonList()
    }
}