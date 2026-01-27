package com.afriasdev.mypoketdex.di

import androidx.paging.PagingData
import androidx.room.Room
import com.afriasdev.mypoketdex.BuildConfig
import com.afriasdev.mypoketdex.data.local.database.PokedexDatabase
import com.afriasdev.mypoketdex.data.remote.api.PokeApiService
import com.afriasdev.mypoketdex.data.repository.PokemonRepositoryImpl
import com.afriasdev.mypoketdex.domain.model.Pokemon
import com.afriasdev.mypoketdex.domain.repository.PokemonRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val dataModule = module {
    // single<PokemonRepository> { MockPokemonRepository() }

    // JSON Configuration
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            PokedexDatabase::class.java,
            PokedexDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    single { get<PokedexDatabase>().pokemonDao() }
    single { get<PokedexDatabase>().remoteKeysDao() }

    // OkHttp Client
    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl(PokeApiService.BASE_URL)
            .client(get())
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    // API Service
    single {
        get<Retrofit>().create(PokeApiService::class.java)
    }

    // Repository
    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }
}


class MockPokemonRepository: PokemonRepository {

    private val mockPokemonList = listOf(
        Pokemon(
            id = 1,
            name = "bulbasaur",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
            types = listOf("grass", "poison")
        ),
        Pokemon(
            id = 4,
            name = "charmander",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
            types = listOf("fire")
        ),
        Pokemon(
            id = 7,
            name = "squirtle",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
            types = listOf("water")
        ),
        Pokemon(
            id = 25,
            name = "pikachu",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
            types = listOf("electric")
        ),
        Pokemon(
            id = 39,
            name = "jigglypuff",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/39.png",
            types = listOf("normal", "fairy")
        ),
        Pokemon(
            id = 94,
            name = "gengar",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/94.png",
            types = listOf("ghost", "poison")
        ),
        Pokemon(
            id = 150,
            name = "mewtwo",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png",
            types = listOf("psychic")
        ),
        Pokemon(
            id = 6,
            name = "charizard",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/6.png",
            types = listOf("fire", "flying")
        )
    )

    override fun getPokemonList(): Flow<PagingData<Pokemon>> {
        return flowOf(PagingData.from(mockPokemonList))
    }

    override suspend fun getPokemonById(id: Int): Pokemon? {
        return mockPokemonList.find { it.id == id }
    }

    override suspend fun searchPokemon(query: String): List<Pokemon> {
        return mockPokemonList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.id.toString() == query
        }
    }
}