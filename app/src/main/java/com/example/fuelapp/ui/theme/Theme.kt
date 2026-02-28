package com.example.fuelapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = FuelPrimaryDark,
    onPrimary = FuelOnPrimaryDark,
    primaryContainer = FuelPrimaryContainerDark,
    onPrimaryContainer = FuelOnPrimaryContainerDark,
    secondary = FuelSecondaryDark,
    background = FuelBackgroundDark,
    surface = FuelSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = FuelPrimary,
    onPrimary = FuelOnPrimary,
    primaryContainer = FuelPrimaryContainer,
    onPrimaryContainer = FuelOnPrimaryContainer,
    secondary = FuelSecondary,
    onSecondary = FuelOnSecondary,
    background = FuelBackground,
    surface = FuelSurface,
    error = FuelError
)

@Composable
fun FuelAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
