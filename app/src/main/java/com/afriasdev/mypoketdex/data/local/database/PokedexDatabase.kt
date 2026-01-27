package com.afriasdev.mypoketdex.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.afriasdev.mypoketdex.data.local.entity.PokemonEntity
import com.afriasdev.mypoketdex.data.local.entity.PokemonRemoteKeys
import com.afriasdev.mypoketdex.data.local.dao.PokemonDao
import com.afriasdev.mypoketdex.data.local.dao.RemoteKeysDao
import com.afriasdev.mypoketdex.data.local.entity.Converters

@Database(
    entities = [
        PokemonEntity::class,
        PokemonRemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "pokedex_database"
    }
}