package com.anshulvyas.csc780.grocerymanagr.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.anshulvyas.csc780.grocerymanagr.Product;

import java.util.List;

/**
 * Created by av7 on 12/3/15.
 */
public class TimelineAdapter extends ArrayAdapter<Product> {
    private List<Product> timelineList;
    Context myContext;
    int mResouce;

    public TimelineAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.mResouce = resource;
        this.timelineList = objects;
    }


}
