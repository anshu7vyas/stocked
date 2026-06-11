package io.github.anshu7vyas.stocked.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.ui.theme.FlagConsumed
import io.github.anshu7vyas.stocked.ui.theme.cardListItem
import io.github.anshu7vyas.stocked.ui.theme.FlagExpired
import io.github.anshu7vyas.stocked.ui.theme.FlagStocked

@Composable
fun TimelineRow(product: Product) {
    val (label, color) = when {
        product.stocked -> R.string.flag_stocked to FlagStocked
        product.expired -> R.string.flag_expired to FlagExpired
        else -> R.string.flag_consumed to FlagConsumed
    }
    Column(modifier = Modifier.cardListItem()) {
        Text(product.name, style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(label),
            color = Color.White,
            modifier = Modifier
                .padding(top = 4.dp)
                .background(color)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
