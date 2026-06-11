package io.github.anshu7vyas.stocked.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.github.anshu7vyas.stocked.R

/** List row: name, category, and a precomputed days-until-expiry label. */
class StockedItemAdapter(
    context: Context,
) : ArrayAdapter<StockedItem>(context, R.layout.list_view_home) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.list_view_home, parent, false)
        val item = getItem(position) ?: return view

        view.findViewById<TextView>(R.id.textView_product_name).text = item.product.name
        view.findViewById<TextView>(R.id.textView_product_category).text =
            " (${item.product.category}) "
        view.findViewById<TextView>(R.id.textView_product_expiry).text =
            if (item.product.expired) context.getString(R.string.expired)
            else context.getString(R.string.expire_in_days, item.daysLeft)
        return view
    }

    fun submitList(items: List<StockedItem>) {
        clear()
        addAll(items)
        notifyDataSetChanged()
    }
}
