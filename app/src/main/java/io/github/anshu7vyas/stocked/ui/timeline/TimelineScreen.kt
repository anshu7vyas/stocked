package io.github.anshu7vyas.stocked.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.ui.common.categoryEmoji
import io.github.anshu7vyas.stocked.ui.home.EmptyState
import io.github.anshu7vyas.stocked.ui.theme.FlagConsumed
import io.github.anshu7vyas.stocked.ui.theme.FlagExpired
import io.github.anshu7vyas.stocked.ui.theme.FlagStocked

/** History of every tracked item with status pills and a waste-visibility stats strip. */
@Composable
fun TimelineScreen(items: List<Product>) {
    if (items.isEmpty()) {
        EmptyState(R.string.timeline_empty, "timeline_list")
        return
    }

    val consumed = items.count { it.consumed }
    val expired = items.count { it.expired }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("timeline_list"),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "📊 " + stringResource(R.string.timeline_stats, consumed, expired),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        items(items, key = Product::id) { product -> TimelineRow(product) }
    }
}

@Composable
fun TimelineRow(product: Product) {
    val (label, color) = when {
        product.stocked -> R.string.flag_stocked to FlagStocked
        product.expired -> R.string.flag_expired to FlagExpired
        else -> R.string.flag_consumed to FlagConsumed
    }
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                categoryEmoji(product.category),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 12.dp),
            )
            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(label),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(color, MaterialTheme.shapes.extraLarge)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}
