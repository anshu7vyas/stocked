package com.anshulvyas.csc780.grocerymanagr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.anshulvyas.csc780.grocerymanagr.Adapters.ProductAdapter;
import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by av7 on 10/15/15.
 */
public class HomeFragment extends Fragment {

    private FloatingActionButton mFAB;
    private List<Product> productList, filterProductList;
    private DBManager dbManager;
    private ListView productListView;
    private Context myContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        filterProductList = new ArrayList<>();

        productList = dbManager.getAllProducts();
        if (productList.size() > 0) {
            Log.i("~!@#HOMEFRAGMENT", productList.get(0).toString());

            for (int i = 0; i < productList.size(); i++) {
                if (!productList.get(i).isShoppingCheck()) {
                    filterProductList.add(productList.get(i));
                }
            }

            for (int i = 0; i < filterProductList.size(); i++) {
                Product productObj = filterProductList.get(i);
                if(getLeftDays(productObj) <= 0) {
                    productObj.setExpired(true);
                    productObj.setStocked(false);
                    productObj.setConsumed(false);
                    dbManager.updateProduct(productObj);
                } else if(getLeftDays(productObj) == 1) {
                    createNotification();
                }
            }


            productListView = (ListView) view.findViewById(R.id.listView_home_product);
            final ProductAdapter productAdapter = new ProductAdapter(getActivity().getBaseContext(), R.layout.list_view_home,
                    filterProductList);
            productListView.setAdapter(productAdapter);

            productAdapter.setNotifyOnChange(true);
            productAdapter.notifyDataSetChanged();

            productListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                    final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                    deleteDialog.setTitle("Delete?");
                    deleteDialog.setMessage("Are you sure you want to delete?");
                    final int viewPosition = position;

                    deleteDialog.setPositiveButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbManager.deleteProduct(productAdapter.getItem(viewPosition));
                            Log.d("DEMO======>", "PRODUCT DELETED");
                            Snackbar.make(view, productAdapter.getItem(viewPosition).getProductName() + " deleted", Snackbar
                                    .LENGTH_LONG).show();

                            productAdapter.remove(productAdapter.getItem(viewPosition));


                            productAdapter.setNotifyOnChange(true);
                            productAdapter.notifyDataSetChanged();
                        }
                    });

                    deleteDialog.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    deleteDialog.show();
                    return false;
                }
            });
            } else{
                Log.i("~!@#HOMEFRAGMENT", "list view not displayed");
            }

            /*
            Instantiate Floating Action button
             */
            mFAB = (FloatingActionButton) view.findViewById(R.id.addFAB);
            mFAB.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    nextActivity();
                }
            });

            return view;
        }

    private void nextActivity() {
        Intent intent = new Intent(getActivity(), AddItemActivity.class);
        startActivity(intent);
    }

    /**
     * Get number of days till expiry. calculates the days between the currentDate an the seleted expiry date.
     * @param productObj Product object
     * @return number of days
     */
    public int getLeftDays(Product productObj) {
        Calendar now = Calendar.getInstance();
        //int expiry = productObj.getExpiryDate() - Util.getDays(now);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Calendar expiry = Calendar.getInstance();
        try {
            expiry.setTime(format.parse(productObj.getExpiryDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateTime dateTimeNow = new DateTime(now.getTime());
        DateTime dateTimeExp = new DateTime(expiry.getTime());

        Days days = Days.daysBetween(dateTimeNow, dateTimeExp);

        return days.getDays() + 1;

    }

    /**
     * Creates notification when called!
     */
    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(myContext, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(myContext, (int) System.currentTimeMillis(), intent, 0);

        Notification notification = new Notification.Builder(myContext)
                .setContentTitle("Item(s) about to expire")
                .setContentText("").setSmallIcon(R.drawable.fooded_icon)
                .setContentIntent(pIntent)
                .addAction(R.drawable.fooded_icon, "Call", pIntent)
                .addAction(R.drawable.ic_home, "Expired", pIntent)
                .addAction(R.drawable.ic_check_white_12dp, "Consumed", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);
    }

}
