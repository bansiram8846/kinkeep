package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KinKeepColorScheme = darkColorScheme(
    primary = CyberTealBright,
    onPrimary = CyberTealDark,
    primaryContainer = CyberTeal,
    onPrimaryContainer = OnCyberTealContainer,
    inversePrimary = Color(0xFF006B5C),
    secondary = IndigoLight,
    onSecondary = Color(0xFF1500A8),
    secondaryContainer = IndigoContainer,
    onSecondaryContainer = OnIndigoContainer,
    tertiary = Color(0xFFFFEEED),
    onTertiary = Color(0xFF680013),
    tertiaryContainer = Color(0xFFFFC8C7),
    onTertiaryContainer = Color(0xFFB9082C),
    error = AlertRedLight,
    onError = Color(0xFF690005),
    errorContainer = AlertRedContainer,
    onErrorContainer = OnAlertRedContainer,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = BorderOutline,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceBright = SurfaceBright
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KinKeepColorScheme,
        typography = Typography,
        content = content
    )
}
