package com.afriasdev.mypoketdex

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afriasdev.mypoketdex.presentation.navigation.PokedexNavigation
import com.afriasdev.mypoketdex.ui.theme.MyPoketDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // Instalar Splash Screen ANTES de super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Configurar animación de salida del splash screen
        setupSplashScreenAnimation(splashScreen)

        enableEdgeToEdge()
        setContent {
            MyPoketDexTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PokedexNavigation()
                }
            }
        }
    }

    private fun setupSplashScreenAnimation(splashScreen: androidx.core.splashscreen.SplashScreen) {
        // Animación de salida del splash screen
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Animación de fade out
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.ALPHA,
                1f,
                0f
            ).apply {
                duration = 500
                interpolator = AnticipateInterpolator()
                doOnEnd { splashScreenView.remove() }
            }

            // Animación de escala
            val scaleX = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.SCALE_X,
                1f,
                1.2f,
                0f
            ).apply {
                duration = 500
                interpolator = AnticipateInterpolator()
            }

            val scaleY = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.SCALE_Y,
                1f,
                1.2f,
                0f
            ).apply {
                duration = 500
                interpolator = AnticipateInterpolator()
            }

            // Iniciar animaciones
            fadeOut.start()
            scaleX.start()
            scaleY.start()
        }
    }
}
