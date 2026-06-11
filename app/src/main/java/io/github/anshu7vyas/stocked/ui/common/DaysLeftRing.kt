package io.github.anshu7vyas.stocked.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.anshu7vyas.stocked.ui.theme.urgencyColor

/** Circular days-left indicator: "2d" in an urgency-colored ring, "!" when expired. */
@Composable
fun DaysLeftRing(daysLeft: Int, modifier: Modifier = Modifier) {
    val color = urgencyColor(daysLeft)
    Box(
        modifier = modifier
            .size(48.dp)
            .border(width = 3.dp, color = color, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (daysLeft <= 0) "!" else "${daysLeft}d",
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
