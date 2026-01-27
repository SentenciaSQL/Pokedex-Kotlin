package com.afriasdev.mypoketdex.di

import com.afriasdev.mypoketdex.presentation.screens.detail.PokemonDetailViewModel
import com.afriasdev.mypoketdex.presentation.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    //viewModel { HomeViewModel(get(), get()) }
    viewModelOf(::HomeViewModel)
    viewModelOf(::PokemonDetailViewModel)
}