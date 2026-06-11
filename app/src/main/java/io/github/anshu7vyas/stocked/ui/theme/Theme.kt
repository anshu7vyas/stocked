package io.github.anshu7vyas.stocked.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Legacy palette, carried over for the like-for-like phase. The Stitch redesign
// (Phase 4) replaces this with a full Material 3 Expressive scheme.
val StockedGreen = Color(0xFF00933B)
val StockedGreenDark = Color(0xFF085108)
val CardBackground = Color(0xFFC8E6C9)
val FlagStocked = Color(0xFF55ACEE)
val FlagExpired = Color(0xFFD73D32)
val FlagConsumed = Color(0xFF20C05C)

private val LightColors = lightColorScheme(
    primary = StockedGreen,
    onPrimary = Color.White,
    secondary = StockedGreen,
    onSecondary = Color.White,
    primaryContainer = CardBackground,
    onPrimaryContainer = Color.Black,
    surface = Color.White,
)

@Composable
fun StockedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}

/** Shared green-on-white top bar styling used by every screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun stockedTopBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
)

/** The legacy card-style list row: full width, outer gap, tinted background, inner padding. */
fun Modifier.cardListItem(): Modifier = this
    .fillMaxWidth()
    .padding(horizontal = 8.dp, vertical = 4.dp)
    .background(CardBackground)
    .padding(12.dp)
