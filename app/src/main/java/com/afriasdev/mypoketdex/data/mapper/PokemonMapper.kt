package com.afriasdev.mypoketdex.data.mapper

import com.afriasdev.mypoketdex.data.remote.dto.PokemonDetailDto
import com.afriasdev.mypoketdex.data.remote.dto.PokemonDto
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.model.Stat

object PokemonMapper {

    fun toDomain(dto: PokemonDto): Pokemon {
        return Pokemon(
            id = dto.id,
            name = dto.name,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${dto.id}.png",
            types = emptyList() // Se llenará con el detalle
        )
    }

    fun toDomain(dto: PokemonDetailDto): Pokemon {
        return Pokemon(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.sprites.other?.officialArtwork?.frontDefault
                ?: dto.sprites.frontDefault
                ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${dto.id}.png",
            types = dto.types.map { it.type.name },
            height = dto.height,
            weight = dto.weight,
            stats = dto.stats.map { statSlot ->
                Stat(
                    name = statSlot.stat.name,
                    value = statSlot.baseStat
                )
            }
        )
    }

}