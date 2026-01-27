package com.afriasdev.mypoketdex.domain.usecase

import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.repository.PokemonRepository

class SearchPokemonUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(query: String): List<Pokemon> {
        return repository.searchPokemon(query)
    }
}