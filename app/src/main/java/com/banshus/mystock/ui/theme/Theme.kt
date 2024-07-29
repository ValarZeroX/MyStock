package com.banshus.mystock.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val lightSchemeGreen = lightColorScheme(
    primary = primaryLightGreen,
    onPrimary = onPrimaryLightGreen,
    primaryContainer = primaryContainerLightGreen,
    onPrimaryContainer = onPrimaryContainerLightGreen,
    secondary = secondaryLightGreen,
    onSecondary = onSecondaryLightGreen,
    secondaryContainer = secondaryContainerLightGreen,
    onSecondaryContainer = onSecondaryContainerLightGreen,
    tertiary = tertiaryLightGreen,
    onTertiary = onTertiaryLightGreen,
    tertiaryContainer = tertiaryContainerLightGreen,
    onTertiaryContainer = onTertiaryContainerLightGreen,
    error = errorLightGreen,
    onError = onErrorLightGreen,
    errorContainer = errorContainerLightGreen,
    onErrorContainer = onErrorContainerLightGreen,
    background = backgroundLightGreen,
    onBackground = onBackgroundLightGreen,
    surface = surfaceLightGreen,
    onSurface = onSurfaceLightGreen,
    surfaceVariant = surfaceVariantLightGreen,
    onSurfaceVariant = onSurfaceVariantLightGreen,
    outline = outlineLightGreen,
    outlineVariant = outlineVariantLightGreen,
    scrim = scrimLightGreen,
    inverseSurface = inverseSurfaceLightGreen,
    inverseOnSurface = inverseOnSurfaceLightGreen,
    inversePrimary = inversePrimaryLightGreen,
    surfaceDim = surfaceDimLightGreen,
    surfaceBright = surfaceBrightLightGreen,
    surfaceContainerLowest = surfaceContainerLowestLightGreen,
    surfaceContainerLow = surfaceContainerLowLightGreen,
    surfaceContainer = surfaceContainerLightGreen,
    surfaceContainerHigh = surfaceContainerHighLightGreen,
    surfaceContainerHighest = surfaceContainerHighestLightGreen
)

private val darkSchemeGreen = darkColorScheme(
    primary = primaryDarkGreen,
    onPrimary = onPrimaryDarkGreen,
    primaryContainer = primaryContainerDarkGreen,
    onPrimaryContainer = onPrimaryContainerDarkGreen,
    secondary = secondaryDarkGreen,
    onSecondary = onSecondaryDarkGreen,
    secondaryContainer = secondaryContainerDarkGreen,
    onSecondaryContainer = onSecondaryContainerDarkGreen,
    tertiary = tertiaryDarkGreen,
    onTertiary = onTertiaryDarkGreen,
    tertiaryContainer = tertiaryContainerDarkGreen,
    onTertiaryContainer = onTertiaryContainerDarkGreen,
    error = errorDarkGreen,
    onError = onErrorDarkGreen,
    errorContainer = errorContainerDarkGreen,
    onErrorContainer = onErrorContainerDarkGreen,
    background = backgroundDarkGreen,
    onBackground = onBackgroundDarkGreen,
    surface = surfaceDarkGreen,
    onSurface = onSurfaceDarkGreen,
    surfaceVariant = surfaceVariantDarkGreen,
    onSurfaceVariant = onSurfaceVariantDarkGreen,
    outline = outlineDarkGreen,
    outlineVariant = outlineVariantDarkGreen,
    scrim = scrimDarkGreen,
    inverseSurface = inverseSurfaceDarkGreen,
    inverseOnSurface = inverseOnSurfaceDarkGreen,
    inversePrimary = inversePrimaryDarkGreen,
    surfaceDim = surfaceDimDarkGreen,
    surfaceBright = surfaceBrightDarkGreen,
    surfaceContainerLowest = surfaceContainerLowestDarkGreen,
    surfaceContainerLow = surfaceContainerLowDarkGreen,
    surfaceContainer = surfaceContainerDarkGreen,
    surfaceContainerHigh = surfaceContainerHighDarkGreen,
    surfaceContainerHighest = surfaceContainerHighestDarkGreen
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun MyStockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeIndex: Int = 1,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> when (themeIndex) {
            1 -> darkScheme
            2 -> darkSchemeGreen
            else -> darkScheme
        }

        else -> when (themeIndex) {
            1 -> lightScheme
            2 -> lightSchemeGreen
            else -> lightScheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}