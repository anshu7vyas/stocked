package com.anshulvyas.csc780.grocerymanagr.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anshulvyas.csc780.grocerymanagr.Product;
import com.anshulvyas.csc780.grocerymanagr.R;

import java.util.List;


/**
 * ShoppingAdapter for the listView in Shopping Fragment
 */
public class ShoppingAdapter extends ArrayAdapter<Product> {
    private List<Product> shoppingList;
    Context myContext;
    int mResource;

    /**
     * Constructor - shopping adapter
     * @param context
     * @param resource
     * @param objects
     */
    public ShoppingAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        Log.i("~!@#SHOPPINGADAPTER", "Constructor reached!");
        this.myContext = context;
        this.shoppingList = objects;
        this.mResource = resource;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        Log.i("~!@#SHOPPINGADAPTER", "getView reached");
        if(convertView == null) {
            Log.i("~!@#SHOPPINGADAPTER", "convert view is null");

            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }


        Log.i("~!@#SHOPPINGADAPTER", "convert view is not null");
        Product shopObj = shoppingList.get(position);

        if(shopObj != null) {
            Log.i("~!@#SHOPPINGADAPTER", shopObj.toString());
            TextView productName = (TextView) convertView.findViewById(R.id.textView_shopping_item_name);

            //only product name is required for the ShoppingItem fragment.
            productName.setText(shopObj.getProductName());
        }
        return convertView;
    }

}
