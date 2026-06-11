package io.github.anshu7vyas.stocked.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.ui.additem.AddItemActivity
import kotlinx.coroutines.launch

/** Pantry tab: stocked items with days-until-expiry, long-press for actions. */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val adapter = StockedItemAdapter(requireContext())
        val listView = view.findViewById<ListView>(R.id.listView_home_product)
        listView.adapter = adapter
        listView.divider = ContextCompat.getDrawable(requireContext(), R.drawable.transparent)

        listView.setOnItemLongClickListener { _, rowView, position, _ ->
            adapter.getItem(position)?.let { showItemActionDialog(it, rowView) }
            true
        }

        view.findViewById<FloatingActionButton>(R.id.addFAB).setOnClickListener {
            startActivity(Intent(requireContext(), AddItemActivity::class.java))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> adapter.submitList(state.items) }
            }
        }

        return view
    }

    private fun showItemActionDialog(item: StockedItem, rowView: View) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.item_action_title, item.product.name))
            .setMessage(R.string.item_action_message)
            .setPositiveButton(R.string.action_consumed) { _, _ ->
                viewModel.markConsumed(item.product)
            }
            .setNegativeButton(R.string.action_expired) { _, _ ->
                viewModel.markExpired(item.product)
            }
            .setNeutralButton(R.string.action_delete) { _, _ ->
                confirmDelete(item, rowView)
            }
            .show()
    }

    private fun confirmDelete(item: StockedItem, rowView: View) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.delete(item.product)
                Snackbar.make(
                    rowView,
                    getString(R.string.deleted_confirmation, item.product.name),
                    Snackbar.LENGTH_LONG,
                ).show()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
