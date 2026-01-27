package com.afriasdev.mypoketdex.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Int = 0,
    val weight: Int = 0,
    val stats: List<Stat> = emptyList()
) {
    val heightInMeters: Float
        get() = height / 10f

    val weightInKg: Float
        get() = weight / 10f
}

data class Stat(
    val name: String,
    val value: Int
) {
    val displayName: String
        get() = when (name) {
            "hp" -> "HP"
            "attack" -> "Attack"
            "defense" -> "Defense"
            "special-attack" -> "Sp. Atk"
            "special-defense" -> "Sp. Def"
            "speed" -> "Speed"
            else -> name.replaceFirstChar { it.uppercase() }
        }
}