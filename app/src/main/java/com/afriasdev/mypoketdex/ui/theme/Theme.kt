package com.afriasdev.mypoketdex.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PokeRed,
    onPrimary = PokeWhite,
    primaryContainer = PokeRedDark,
    onPrimaryContainer = PokeWhite,
    secondary = TypeElectric,
    onSecondary = PokeBlack,
    secondaryContainer = TypeElectric,
    onSecondaryContainer = PokeBlack,
    tertiary = TypeWater,
    onTertiary = PokeWhite,
    background = Color(0xFF121212),
    onBackground = PokeWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = PokeWhite,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = PokeGray,
    outline = PokeDarkGray,
    error = Color(0xFFCF6679),
    onError = PokeBlack
)

private val LightColorScheme = lightColorScheme(
    primary = PokeRed,
    onPrimary = PokeWhite,
    primaryContainer = PokeRedDark,
    onPrimaryContainer = PokeWhite,
    secondary = TypeElectric,
    onSecondary = PokeBlack,
    secondaryContainer = TypeElectric,
    onSecondaryContainer = PokeBlack,
    tertiary = TypeWater,
    onTertiary = PokeWhite,
    background = PokeWhite,
    onBackground = PokeBlack,
    surface = Color(0xFFFAFAFA),
    onSurface = PokeBlack,
    surfaceVariant = PokeGray,
    onSurfaceVariant = PokeDarkGray,
    outline = PokeDarkGray,
    error = Color(0xFFB00020),
    onError = PokeWhite
)

@Composable
fun MyPoketDexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}