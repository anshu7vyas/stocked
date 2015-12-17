package com.anshulvyas.csc780.grocerymanagr.Adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
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
 * TimelineAdapter for the listView in Timeline Fragment.
 */
public class TimelineAdapter extends ArrayAdapter<Product> {
    private List<Product> timelineList;
    Context myContext;
    int mResource;

    /**
     * Constructor - TimelineAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public TimelineAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        Log.i("~!@#TIMELINEADAPTER", "Constructor reached!");
        this.myContext = context;
        this.timelineList = objects;
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

        Log.i("~!@#TIMELINEADAPTER", "getView reached");
        if(convertView == null) {
            Log.i("~!@#TIMELINEADAPTER", "convert view is null");

            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }

        Log.i("~!@#TIMELINEADAPTER", "convert view is not null");
        Product productObj = timelineList.get(position);

        if(productObj != null) {
            Log.i("~!@#TIMELINEADAPTER", productObj.toString());
            TextView timelineName = (TextView) convertView.findViewById(R.id.textView_timeline_item_name);
            TextView timelineFlag = (TextView) convertView.findViewById(R.id.textView_timeline_item_flag);


            timelineName.setText(productObj.getProductName());
            Log.i("~!@$#TIMELINEADAPTER", productObj.isStocked() + "");
            Log.i("~!@$#TIMELINEADAPTER", productObj.isExpired() + "");

            //check whether the item is stocked, expired or consumed setting the tag color accordingly
            if(productObj.isStocked()) {
                timelineFlag.setText("STOCKED");
                timelineFlag.setBackgroundColor(ContextCompat.getColor(myContext, R.color.stocked));
            } else if (productObj.isExpired()) {
                timelineFlag.setText("EXPIRED");
                timelineFlag.setBackgroundColor(ContextCompat.getColor(myContext, R.color.expired));
            } else {
                timelineFlag.setText("CONSUMED");
                timelineFlag.setBackgroundColor(ContextCompat.getColor(myContext, R.color.consumed));
            }
        }
        return convertView;
    }
}