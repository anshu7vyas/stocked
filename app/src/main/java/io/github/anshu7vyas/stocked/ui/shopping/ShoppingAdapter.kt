package io.github.anshu7vyas.stocked.ui.shopping

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.github.anshu7vyas.stocked.R
import io.github.anshu7vyas.stocked.data.Product

class ShoppingAdapter(
    context: Context,
) : ArrayAdapter<Product>(context, R.layout.list_view_shopping) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.list_view_shopping, parent, false)
        getItem(position)?.let { product ->
            view.findViewById<TextView>(R.id.textView_shopping_item_name).text = product.name
        }
        return view
    }

    fun submitList(items: List<Product>) {
        clear()
        addAll(items)
        notifyDataSetChanged()
    }
}
