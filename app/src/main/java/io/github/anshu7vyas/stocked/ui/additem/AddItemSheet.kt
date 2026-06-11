package io.github.anshu7vyas.stocked.ui.additem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.ui.common.categoryEmoji
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Bottom sheet for adding a pantry item: name, emoji category chips, and
 * quick-pick expiry (+3 days / +1 week / +2 weeks / custom date).
 * [initialName] supports the shopping-list "move to pantry" flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, expiry: LocalDate) -> Unit,
    initialName: String = "",
) {
    val categories = stringArrayResource(R.array.items_list)
    var name by rememberSaveable { mutableStateOf(initialName) }
    var category by rememberSaveable { mutableStateOf(categories.first()) }
    var expiry by rememberSaveable { mutableStateOf<String?>(null) } // ISO date
    var showDatePicker by remember { mutableStateOf(false) }
    var showNameError by remember { mutableStateOf(false) }
    var showExpiryError by remember { mutableStateOf(false) }
    // Stable baseline for the quick-pick chips: recomputing now() per composition
    // would deselect a chip if the clock crossed midnight while the sheet is open.
    val sheetToday = remember { LocalDate.now() }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.add_item), style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    showNameError = false
                },
                label = { Text(stringResource(R.string.item_name_hint)) },
                isError = showNameError,
                supportingText = if (showNameError) {
                    { Text(stringResource(R.string.error_item_name_required)) }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("item_name_field"),
            )

            Text(stringResource(R.string.item_category), style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories.toList()) { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        label = { Text("${categoryEmoji(option)} $option") },
                    )
                }
            }

            Text(stringResource(R.string.expires_in), style = MaterialTheme.typography.titleMedium)
            if (showExpiryError) {
                Text(
                    stringResource(R.string.error_expiry_required),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickExpiryChip(R.string.expiry_plus_3_days, expiry, sheetToday.plusDays(3)) { expiry = it; showExpiryError = false }
                QuickExpiryChip(R.string.expiry_plus_1_week, expiry, sheetToday.plusDays(7)) { expiry = it; showExpiryError = false }
                QuickExpiryChip(R.string.expiry_plus_2_weeks, expiry, sheetToday.plusDays(14)) { expiry = it; showExpiryError = false }
            }
            SuggestionChip(
                onClick = { showDatePicker = true },
                label = {
                    Text(
                        expiry?.let { stringResource(R.string.expiry_custom_set, LocalDate.parse(it).format(DATE_FORMAT)) }
                            ?: stringResource(R.string.expiry_custom),
                    )
                },
                modifier = Modifier.testTag("custom_date_chip"),
            )

            Button(
                onClick = {
                    when {
                        name.isBlank() -> showNameError = true
                        expiry == null -> showExpiryError = true
                        else -> onSave(name.trim(), category, LocalDate.parse(expiry))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_item_button"),
            ) {
                Text(stringResource(R.string.add_to_pantry))
            }
        }
    }

    if (showDatePicker) {
        val tomorrow = LocalDate.now().plusDays(1)
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = tomorrow.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
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
                        showExpiryError = false
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

@Composable
private fun QuickExpiryChip(
    labelRes: Int,
    current: String?,
    date: LocalDate,
    onPick: (String) -> Unit,
) {
    val value = date.toString()
    FilterChip(
        selected = current == value,
        onClick = { onPick(value) },
        label = { Text(stringResource(labelRes)) },
    )
}

private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")
