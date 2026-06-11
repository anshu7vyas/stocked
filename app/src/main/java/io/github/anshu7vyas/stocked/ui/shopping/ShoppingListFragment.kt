package io.github.anshu7vyas.stocked.ui.shopping

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import io.github.anshu7vyas.stocked.R
import kotlinx.coroutines.launch

/** Shopping-list tab: items to buy on the next grocery run. */
@AndroidEntryPoint
class ShoppingListFragment : Fragment() {

    private val viewModel: ShoppingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_shopping_list, container, false)

        val adapter = ShoppingAdapter(requireContext())
        val listView = view.findViewById<ListView>(R.id.listView_shopping_product)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val product = adapter.getItem(position) ?: return@setOnItemLongClickListener false
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.delete(product) }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
            true
        }

        view.findViewById<FloatingActionButton>(R.id.itemFAB).setOnClickListener {
            startActivity(Intent(requireContext(), ShoppingItemActivity::class.java))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect(adapter::submitList)
            }
        }

        return view
    }
}
