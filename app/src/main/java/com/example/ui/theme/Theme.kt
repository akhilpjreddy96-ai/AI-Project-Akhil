package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    secondary = IndianGold,
    tertiary = IncomeBlue,
    background = DeepCharcoal,
    surface = CardBackground,
    onPrimary = DeepCharcoal,
    onSecondary = DeepCharcoal,
    onTertiary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = IndianGold,
    tertiary = IncomeBlue,
    background = PureWhite,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = DeepCharcoal,
    onTertiary = DeepCharcoal,
    onBackground = DeepCharcoal,
    onSurface = DeepCharcoal,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force stunning dark theme for premium financial dashboard feeling
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
