package com.afriasdev.mypoketdex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.model.Stat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String, // JSON string de lista
    val height: Int,
    val weight: Int,
    val stats: String // JSON string de lista de stats
)

// Converters para tipos complejos
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList()
        else json.decodeFromString(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun fromStatList(value: String): List<StatDto> {
        return if (value.isEmpty()) emptyList()
        else json.decodeFromString(value)
    }

    @TypeConverter
    fun toStatList(list: List<StatDto>): String {
        return json.encodeToString(list)
    }
}

@Serializable
data class StatDto(
    val name: String,
    val value: Int
)

// Extension para convertir Entity a Domain
fun PokemonEntity.toDomain(): Pokemon {
    val json = Json { ignoreUnknownKeys = true }
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = json.decodeFromString(types),
        height = height,
        weight = weight,
        stats = json.decodeFromString<List<StatDto>>(stats).map {
            Stat(name = it.name, value = it.value)
        }
    )
}

// Extension para convertir Domain a Entity
fun Pokemon.toEntity(): PokemonEntity {
    val json = Json { ignoreUnknownKeys = true }
    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = json.encodeToString(types),
        height = height,
        weight = weight,
        stats = json.encodeToString(stats.map { StatDto(it.name, it.value) })
    )
}