package com.afriasdev.mypoketdex.di

import com.afriasdev.mypoketdex.domain.usecase.GetPokemonDetailUseCase
import com.afriasdev.mypoketdex.domain.usecase.GetPokemonListUseCase
import com.afriasdev.mypoketdex.domain.usecase.SearchPokemonUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // Use Cases
//    factory { GetPokemonListUseCase(get()) }
//    factory { SearchPokemonUseCase(get()) }
    factoryOf(::GetPokemonListUseCase)
    factoryOf(::SearchPokemonUseCase)
    factoryOf(::GetPokemonDetailUseCase)
}