package com.afriasdev.mypoketdex.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    @SerialName("count")
    val count: Int,

    @SerialName("next")
    val next: String?,

    @SerialName("previous")
    val previous: String?,

    @SerialName("results")
    val results: List<PokemonDto>
)

@Serializable
data class PokemonDto(
    @SerialName("name")
    val name: String,

    @SerialName("url")
    val url: String
) {
    // Extraer ID de la URL: https://pokeapi.co/api/v2/pokemon/25/
    val id: Int
        get() = url.trimEnd('/').split("/").last().toIntOrNull() ?: 0
}