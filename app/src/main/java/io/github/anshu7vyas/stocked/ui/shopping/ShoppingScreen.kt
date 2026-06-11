package io.github.anshu7vyas.stocked.ui.shopping

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.data.Product
import io.github.anshu7vyas.stocked.ui.home.EmptyState

/**
 * Shopping list: round check buttons; checking an item offers to move it to the
 * pantry (which opens the add sheet prefilled). Quick-add field pinned below.
 */
@Composable
fun ShoppingScreen(
    items: List<Product>,
    onQuickAdd: (String) -> Unit,
    onDelete: (Product) -> Unit,
    onMoveToPantry: (Product) -> Unit,
) {
    // The id of the item the user just checked. If that item leaves the list the
    // id simply matches no row, so stale values are harmless.
    var checkedId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            if (items.isEmpty()) {
                EmptyState(R.string.shopping_empty, "shopping_list")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("shopping_list"),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(items, key = Product::id) { product ->
                        ShoppingRow(
                            product = product,
                            checked = checkedId == product.id,
                            onCheck = {
                                checkedId = if (checkedId == product.id) null else product.id
                            },
                            onMoveToPantry = { onMoveToPantry(product) },
                            onDelete = { onDelete(product) },
                        )
                    }
                }
            }
        }
        QuickAddField(onQuickAdd)
    }
}

@Composable
private fun ShoppingRow(
    product: Product,
    checked: Boolean,
    onCheck: () -> Unit,
    onMoveToPantry: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (checked) MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = checked, onClick = onCheck)
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (checked) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f),
            )
            if (checked) {
                AssistChip(
                    onClick = onMoveToPantry,
                    label = { Text(stringResource(R.string.move_to_pantry_prompt)) },
                    modifier = Modifier.testTag("move_to_pantry_chip"),
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = stringResource(R.string.action_delete),
                )
            }
        }
    }
}

@Composable
private fun QuickAddField(onQuickAdd: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        placeholder = { Text(stringResource(R.string.shopping_quick_add_hint)) },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        trailingIcon = {
            IconButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onQuickAdd(name.trim())
                        name = ""
                    }
                },
                modifier = Modifier.testTag("quick_add_button"),
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_item))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("quick_add_field"),
    )
}
