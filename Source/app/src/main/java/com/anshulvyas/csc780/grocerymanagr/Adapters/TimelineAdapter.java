package com.anshulvyas.csc780.grocerymanagr.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;
import com.anshulvyas.csc780.grocerymanagr.Product;
import com.anshulvyas.csc780.grocerymanagr.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by av7 on 12/13/15.
 */
public class TimelineAdapter extends ArrayAdapter<Product> {
    private List<Product> timelineList;
    Context myContext;
    int mResource;


    public TimelineAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        Log.i("~!@#TIMELINEADAPTER", "Constructor reached!");
        this.myContext = context;
        this.timelineList = objects;
        this.mResource = resource;
    }

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

//            Calendar now = Calendar.getInstance();
//            //int expiry = productObj.getExpiryDate() - Util.getDays(now);
//
//            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
//            Calendar expiry = Calendar.getInstance();
//            try {
//                expiry.setTime(format.parse(productObj.getExpiryDate()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            DateTime dateTimeNow = new DateTime(now.getTime());
//            DateTime dateTimeExp = new DateTime(expiry.getTime());
//
//            Days days = Days.daysBetween(dateTimeNow, dateTimeExp);
//
//            if (days.getDays() <= 0) {
//                DBManager dbManager = new DBManager(myContext);
//                productObj.setExpired(true);
//                dbManager.updateProduct(productObj);
//            }
//
//            Log.i("~!@#DAYS", days.getDays() + "");
        }
        return convertView;
    }

//    private int getLeftDays(Calendar then, Calendar now) {
//        long diff = then.getTimeInMillis() - now.getTimeInMillis();
//        int days = (int) diff / (24 * 60 * 60 * 1000);
//        return days;
//    }

}