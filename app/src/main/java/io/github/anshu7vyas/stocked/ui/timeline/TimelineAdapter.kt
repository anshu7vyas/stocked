package io.github.anshu7vyas.stocked.ui.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.data.Product

class TimelineAdapter(
    context: Context,
) : ArrayAdapter<Product>(context, R.layout.list_view_timeline) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.list_view_timeline, parent, false)
        val product = getItem(position) ?: return view

        view.findViewById<TextView>(R.id.textView_timeline_item_name).text = product.name
        val flag = view.findViewById<TextView>(R.id.textView_timeline_item_flag)
        val (label, color) = when {
            product.stocked -> R.string.flag_stocked to R.color.stocked
            product.expired -> R.string.flag_expired to R.color.expired
            else -> R.string.flag_consumed to R.color.consumed
        }
        flag.setText(label)
        flag.setBackgroundColor(ContextCompat.getColor(context, color))
        return view
    }

    fun submitList(items: List<Product>) {
        clear()
        addAll(items)
        notifyDataSetChanged()
    }
}
