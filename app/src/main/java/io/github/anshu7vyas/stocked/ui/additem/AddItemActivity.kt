package io.github.anshu7vyas.stocked.ui.additem

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.anshu7vyas.stocked.R
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Add a pantry item: category, name, and expiry date. */
@AndroidEntryPoint
class AddItemActivity : AppCompatActivity() {

    private val viewModel: AddItemViewModel by viewModels()

    private var expiryDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.add_item)

        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        val categorySpinner = findViewById<Spinner>(R.id.spinner)
        val nameField = findViewById<EditText>(R.id.editText_itemName)
        val expiryLabel = findViewById<TextView>(R.id.textView_datePicker_expiry)

        expiryLabel.setOnClickListener { showDatePicker(expiryLabel) }

        findViewById<FloatingActionButton>(R.id.FAB_check).setOnClickListener {
            val name = nameField.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(
                    coordinatorLayout, R.string.error_item_name_required, Snackbar.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }
            val expiry = expiryDate
            if (expiry == null) {
                Snackbar.make(
                    coordinatorLayout, R.string.error_expiry_required, Snackbar.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }
            viewModel.addStockedProduct(name, categorySpinner.selectedItem.toString(), expiry)
            finish()
        }
    }

    private fun showDatePicker(expiryLabel: TextView) {
        val today = LocalDate.now()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                expiryDate = LocalDate.of(year, month + 1, dayOfMonth).also {
                    expiryLabel.text = it.format(DATE_DISPLAY_FORMAT)
                }
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth,
        ).apply {
            setTitle(getString(R.string.select_date))
            // No same-day expiries: minimum selectable date is tomorrow, computed in
            // local time (a raw millis offset drifts around UTC midnight and DST).
            datePicker.minDate = today.plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.show()
    }

    private companion object {
        val DATE_DISPLAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")
    }
}
