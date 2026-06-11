package io.github.anshu7vyas.stocked.ui.shopping

import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.anshu7vyas.stocked.R

/** Add an item to buy on the next grocery run. */
@AndroidEntryPoint
class ShoppingItemActivity : AppCompatActivity() {

    private val viewModel: ShoppingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.cart_item)

        val coordinatorLayout =
            findViewById<CoordinatorLayout>(R.id.shoopingCoordinatorLayout)
        val nameField = findViewById<EditText>(R.id.editText_shopping_item_name)

        findViewById<FloatingActionButton>(R.id.FAB_check_shop).setOnClickListener {
            val name = nameField.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(
                    coordinatorLayout, R.string.error_item_name_required, Snackbar.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }
            viewModel.addItem(name)
            finish()
        }
    }
}
