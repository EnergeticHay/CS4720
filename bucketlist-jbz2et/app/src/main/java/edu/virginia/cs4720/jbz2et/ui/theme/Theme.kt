package edu.virginia.cs4720.jbz2et.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = Orange,
    onPrimaryContainer = Navy,
    secondary = Orange,
    onSecondary = Navy,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Stone,
    onSurfaceVariant = Slate,
    error = Brick,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Periwinkle,
    onPrimary = Navy,
    primaryContainer = Orange,
    onPrimaryContainer = Navy,
    secondary = Orange,
    onSecondary = Navy,
    background = Night,
    onBackground = Fog,
    surface = NightSurface,
    onSurface = Fog,
    surfaceVariant = NightVariant,
    onSurfaceVariant = Mist,
    error = Rose,
    onError = Night
)

@Composable
fun Bucketlistjbz2etTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}