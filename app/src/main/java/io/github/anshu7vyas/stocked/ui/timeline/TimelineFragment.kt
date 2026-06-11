package io.github.anshu7vyas.stocked.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.anshu7vyas.stocked.R
import kotlinx.coroutines.launch

/** Timeline tab: every tracked item with its stocked/consumed/expired status. */
@AndroidEntryPoint
class TimelineFragment : Fragment() {

    private val viewModel: TimelineViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_timeline, container, false)

        val adapter = TimelineAdapter(requireContext())
        view.findViewById<ListView>(R.id.listView_timeline_product).adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect(adapter::submitList)
            }
        }

        return view
    }
}
