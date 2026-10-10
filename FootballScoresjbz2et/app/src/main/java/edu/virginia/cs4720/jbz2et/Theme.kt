package edu.virginia.cs4720.jbz2et

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import edu.virginia.cs4720.jbz2et.Ink
import edu.virginia.cs4720.jbz2et.Lavender
import edu.virginia.cs4720.jbz2et.LavenderCard
import edu.virginia.cs4720.jbz2et.LavenderChip
import edu.virginia.cs4720.jbz2et.NightCard
import edu.virginia.cs4720.jbz2et.NightChip
import edu.virginia.cs4720.jbz2et.NightPurple
import edu.virginia.cs4720.jbz2et.Purple
import edu.virginia.cs4720.jbz2et.PurpleLight
import edu.virginia.cs4720.jbz2et.White

private val DarkColorScheme = darkColorScheme(
    primary = PurpleLight,
    onPrimary = Ink,
    secondary = PurpleLight,
    onSecondary = Ink,
    secondaryContainer = NightChip,
    onSecondaryContainer = LavenderChip,
    background = NightPurple,
    surface = NightPurple,
    surfaceContainerHighest = NightCard
)

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = White,
    secondary = Purple,
    onSecondary = White,
    secondaryContainer = LavenderChip,
    onSecondaryContainer = Ink,
    background = Lavender,
    surface = Lavender,
    surfaceContainerHighest = LavenderCard
)

@Composable
fun FootballScoresjbz2etTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}