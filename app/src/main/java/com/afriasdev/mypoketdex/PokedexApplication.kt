package com.afriasdev.mypoketdex

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.afriasdev.mypoketdex.di.appModule
import com.afriasdev.mypoketdex.di.dataModule
import com.afriasdev.mypoketdex.di.domainModule
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class PokedexApplication: Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("PokedexApplication: Iniciando aplicación")

        // Inicializar Koin
        try {
            startKoin {
                androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
                androidContext(this@PokedexApplication)
                modules(
                    listOf(
                        appModule,
                        domainModule,
                        dataModule
                    )
                )
            }
            Timber.d("PokedexApplication: Koin iniciado correctamente")
        } catch (e: Exception) {
            Timber.e(e, "Error al inicializar Koin")
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            OkHttpClient.Builder()
                                .build()
                        }
                    )
                )
            }
            .build()
    }
}