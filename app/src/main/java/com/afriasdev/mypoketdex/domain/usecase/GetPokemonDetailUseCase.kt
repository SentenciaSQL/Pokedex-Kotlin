package com.afriasdev.mypoketdex.domain.usecase

import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.repository.PokemonRepository

class GetPokemonDetailUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(id: Int): Pokemon? {
        return repository.getPokemonById(id)
    }
}