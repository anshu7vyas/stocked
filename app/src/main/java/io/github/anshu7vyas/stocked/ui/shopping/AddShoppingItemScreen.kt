package io.github.anshu7vyas.stocked.ui.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.ui.theme.stockedTopBarColors
import kotlinx.coroutines.launch

/** Add an item to buy on the next grocery run. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingItemScreen(
    onDone: () -> Unit,
    viewModel: ShoppingViewModel = hiltViewModel(),
) {
    var name by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val nameRequired = stringResource(R.string.error_item_name_required)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cart_item)) },
                colors = stockedTopBarColors(),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (name.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar(nameRequired) }
                    } else {
                        viewModel.addItem(name.trim())
                        onDone()
                    }
                },
                modifier = Modifier.testTag("save_shopping_fab"),
            ) {
                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.cart_item))
            }
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Text(
                stringResource(R.string.item_name),
                style = MaterialTheme.typography.titleMedium,
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("shopping_name_field"),
            )
        }
    }
}
