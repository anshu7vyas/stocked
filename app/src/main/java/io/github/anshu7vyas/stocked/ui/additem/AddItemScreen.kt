package io.github.anshu7vyas.stocked.ui.additem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.ui.theme.stockedTopBarColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

/** Add a pantry item: category, name, and expiry date (min: tomorrow). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onDone: () -> Unit,
    viewModel: AddItemViewModel = hiltViewModel(),
) {
    val categories = stringArrayResource(R.array.items_list)
    var category by rememberSaveable { mutableStateOf(categories.first()) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var expiry by rememberSaveable { mutableStateOf<String?>(null) } // ISO date
    var showDatePicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val nameRequired = stringResource(R.string.error_item_name_required)
    val expiryRequired = stringResource(R.string.error_expiry_required)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_item)) },
                colors = stockedTopBarColors(),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val currentExpiry = expiry
                    when {
                        name.isBlank() ->
                            scope.launch { snackbarHostState.showSnackbar(nameRequired) }
                        currentExpiry == null ->
                            scope.launch { snackbarHostState.showSnackbar(expiryRequired) }
                        else -> {
                            viewModel.addStockedProduct(
                                name.trim(), category, LocalDate.parse(currentExpiry),
                            )
                            onDone()
                        }
                    }
                },
                modifier = Modifier.testTag("save_item_fab"),
            ) {
                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.add_item))
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
                stringResource(R.string.item_category),
                style = MaterialTheme.typography.titleMedium,
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .testTag("category_dropdown"),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }

            Text(
                stringResource(R.string.item_name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("item_name_field"),
            )

            Text(
                stringResource(R.string.item_days),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("set_date_button"),
            ) {
                Text(
                    expiry?.let { LocalDate.parse(it).format(DATE_DISPLAY_FORMAT) }
                        ?: stringResource(R.string.select_date),
                )
            }
        }
    }

    if (showDatePicker) {
        val tomorrow = LocalDate.now().plusDays(1)
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = tomorrow.atStartOfDay(ZoneOffset.UTC)
                .toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                // No same-day expiries: minimum selectable date is tomorrow (legacy rule).
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    !Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC)
                        .toLocalDate().isBefore(tomorrow)
            },
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        expiry = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC)
                            .toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private val DATE_DISPLAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")
