package com.afriasdev.mypoketdex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.afriasdev.mypoketdex.data.local.entity.PokemonRemoteKeys

@Dao
interface RemoteKeysDao {
    @Query("SELECT * FROM pokemon_remote_keys WHERE pokemonId = :id")
    suspend fun getRemoteKeys(id: Int): PokemonRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<PokemonRemoteKeys>)

    @Query("DELETE FROM pokemon_remote_keys")
    suspend fun clearAll()
}