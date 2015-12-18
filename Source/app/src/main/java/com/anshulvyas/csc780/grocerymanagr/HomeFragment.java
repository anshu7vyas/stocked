package com.anshulvyas.csc780.grocerymanagr;


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
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
 * Hosts the listView of items that user wants to keep track of - with number of days before item expires
 */
public class HomeFragment extends Fragment {

    private FloatingActionButton mFAB;
    private List<Product> productList, filterProductList;
    private DBManager dbManager;
    private ListView productListView;
    private Context myContext;
    public List<Integer> notificationList;

    /**
     * Called when a fragment is first attached to its context.
     * @param context - to be attached to
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = context;
    }


    /**
     * Called to do initial creation of a fragment. (overridden)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DBManager(getActivity());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        filterProductList = new ArrayList<>();

        productList = dbManager.getAllStockedProducts();
        if (productList.size() > 0) {
            Log.i("~!@#HOMEFRAGMENT", productList.get(0).toString());

            for (int i = 0; i < productList.size(); i++) {
                if (!productList.get(i).isShoppingCheck()) {
                    filterProductList.add(productList.get(i));
                }
            }

            notificationList = new ArrayList<>();
            notificationList.clear();

            for (int i = 0; i < filterProductList.size(); i++) {
                Product productObj = filterProductList.get(i);
                if(getLeftDays(productObj) <= 0) {
                    productObj.setExpired(true);
                    productObj.setStocked(false);
                    productObj.setConsumed(false);
                    dbManager.updateProduct(productObj);
                } else if(getLeftDays(productObj) == 1) {
                    if(productObj != null) {
                        notificationList.add(productObj.getProductId());
                    }
                }
            }

            createNotification();

            productListView = (ListView) view.findViewById(R.id.listView_home_product);
            final ProductAdapter productAdapter = new ProductAdapter(getActivity().getBaseContext(), R.layout.list_view_home,
                    filterProductList);
            productListView.setAdapter(productAdapter);
            productListView.setDivider(this.getResources().getDrawable(R.drawable.transparent));

            productAdapter.setNotifyOnChange(true);
            productAdapter.notifyDataSetChanged();

            /**
             * Calling a delete dialog box to confirm user's action before deleting the item from the Adapter.
             */
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

        /**
         * Instantiate Floating Action button
         */
        mFAB = (FloatingActionButton) view.findViewById(R.id.addFAB);
        mFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    nextActivity();
                }
            });

        return view;
    }

    /**
     * Workaround for calling an intent from the fragment to an activity.
     */
    private void nextActivity() {
        Intent intent = new Intent(getActivity(), AddItemActivity.class);
        startActivity(intent);
    }

    /**
     * Get number of days till expiry. calculates the days between the currentDate an the selected expiry date.
     * @param productObj Product object
     * @return number of days
     */
    public int getLeftDays(Product productObj) {
        Calendar now = Calendar.getInstance();

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
        Intent intent = new Intent(myContext, HomeActivity.class);
        Integer[] simpleArray = new Integer[notificationList.size()];
        notificationList.toArray(simpleArray);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra( "List", simpleArray);
        PendingIntent pIntent = PendingIntent.getActivity(myContext, (int) System.currentTimeMillis(), intent, 0);


        Notification notification = new Notification.Builder(myContext)
                .setContentTitle("Stocked!")
                .setContentText(String.valueOf(notificationList.size()) + " Item(s) about to expire")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);
    }

//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        final ProductAdapter productAdapter = new ProductAdapter(getActivity().getBaseContext(), R.layout.list_view_home,
//                filterProductList);
//        productListView.setAdapter(productAdapter);
//        productListView.setDivider(this.getResources().getDrawable(R.drawable.transparent));
//        productListView.setDividerHeight(20);
//
//        productAdapter.setNotifyOnChange(true);
//        productAdapter.notifyDataSetChanged();
//    }

}
