package io.github.anshu7vyas.stocked.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// "Fresh Expressive Tracker" tokens from the Stitch DESIGN.md (docs/design/).
val FreshGreen = Color(0xFF006B29)
val OnFreshGreen = Color(0xFFFFFFFF)
val GreenContainer = Color(0xFFA8F37D)
val OnGreenContainer = Color(0xFF1F5100)
val AmberTertiary = Color(0xFF765700)
val AmberContainer = Color(0xFFFFDFA0)
val OnAmberContainer = Color(0xFF5C4300)
val MintedSurface = Color(0xFFF7FBF2)
val OnMintedSurface = Color(0xFF181D18)

// Status / urgency accents.
val UrgencyFresh = Color(0xFF008A37)
val UrgencyWarning = Color(0xFFB88900)
val UrgencyExpired = Color(0xFFBA1A1A)
val FlagStocked = Color(0xFF55ACEE)
val FlagExpired = UrgencyExpired
val FlagConsumed = Color(0xFF20C05C)

private val LightColors = lightColorScheme(
    primary = FreshGreen,
    onPrimary = OnFreshGreen,
    primaryContainer = Color(0xFF84FB95),
    onPrimaryContainer = Color(0xFF00531E),
    inversePrimary = Color(0xFF67DE7C),
    secondary = Color(0xFF2B6C00),
    onSecondary = Color.White,
    secondaryContainer = GreenContainer,
    onSecondaryContainer = OnGreenContainer,
    tertiary = AmberTertiary,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    error = UrgencyExpired,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    background = MintedSurface,
    onBackground = OnMintedSurface,
    surface = MintedSurface,
    onSurface = OnMintedSurface,
    surfaceVariant = Color(0xFFE0E4DB),
    onSurfaceVariant = Color(0xFF3E4A3D),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1F5EC),
    surfaceContainer = Color(0xFFEBEFE6),
    surfaceContainerHigh = Color(0xFFE5EAE1),
    surfaceContainerHighest = Color(0xFFE0E4DB),
    outline = Color(0xFF6E7A6C),
    outlineVariant = Color(0xFFBDCAB9),
    inverseSurface = Color(0xFF2D322C),
    inverseOnSurface = Color(0xFFEEF2E9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF67DE7C),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF00531E),
    onPrimaryContainer = Color(0xFF84FB95),
    secondary = Color(0xFF90D967),
    onSecondary = Color(0xFF133800),
    secondaryContainer = Color(0xFF1F5100),
    onSecondaryContainer = Color(0xFFABF680),
    tertiary = Color(0xFFFBBC00),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5C4300),
    onTertiaryContainer = Color(0xFFFFDFA0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101510),
    onBackground = Color(0xFFE0E4DB),
    surface = Color(0xFF101510),
    onSurface = Color(0xFFE0E4DB),
    surfaceVariant = Color(0xFF3E4A3D),
    onSurfaceVariant = Color(0xFFBDCAB9),
    surfaceContainerLowest = Color(0xFF0B0F0B),
    surfaceContainerLow = Color(0xFF181D18),
    surfaceContainer = Color(0xFF1C211C),
    surfaceContainerHigh = Color(0xFF262B26),
    surfaceContainerHighest = Color(0xFF313631),
    outline = Color(0xFF889583),
    outlineVariant = Color(0xFF3E4A3D),
    inverseSurface = Color(0xFFE0E4DB),
    inverseOnSurface = Color(0xFF2D322C),
)

/** Pill-forward shape language: soft cards, tactile sheets. */
private val StockedShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun StockedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Brand green is the identity; Material You can be offered as a setting later.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = StockedTypography,
        shapes = StockedShapes,
        content = content,
    )
}

/** Urgency color for a days-left value, matching the design's traffic-light logic. */
fun urgencyColor(daysLeft: Int): Color = when {
    daysLeft <= 0 -> UrgencyExpired
    daysLeft <= 3 -> UrgencyWarning
    else -> UrgencyFresh
}
