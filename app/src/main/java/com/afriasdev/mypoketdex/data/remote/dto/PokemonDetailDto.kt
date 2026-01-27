package com.afriasdev.mypoketdex.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetailDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("height")
    val height: Int,

    @SerialName("weight")
    val weight: Int,

    @SerialName("types")
    val types: List<TypeSlot>,

    @SerialName("stats")
    val stats: List<StatSlot>,

    @SerialName("sprites")
    val sprites: Sprites
)

@Serializable
data class TypeSlot(
    @SerialName("slot")
    val slot: Int,

    @SerialName("type")
    val type: Type
)

@Serializable
data class Type(
    @SerialName("name")
    val name: String
)

@Serializable
data class StatSlot(
    @SerialName("base_stat")
    val baseStat: Int,

    @SerialName("stat")
    val stat: Stat
)

@Serializable
data class Stat(
    @SerialName("name")
    val name: String
)

@Serializable
data class Sprites(
    @SerialName("other")
    val other: Other? = null,

    @SerialName("front_default")
    val frontDefault: String? = null
)

@Serializable
data class Other(
    @SerialName("official-artwork")
    val officialArtwork: OfficialArtwork? = null
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default")
    val frontDefault: String?
)