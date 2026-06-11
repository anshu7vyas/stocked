package io.github.anshu7vyas.stocked;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import io.github.anshu7vyas.stocked.Adapters.ProductAdapter;
import io.github.anshu7vyas.stocked.Model.DBManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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


            /**
             * making list of items that are in HomeFragment and not in ShoppingFragment
             */
            for (int i = 0; i < productList.size(); i++) {
                if (!productList.get(i).isShoppingCheck()) {
                    filterProductList.add(productList.get(i));
                }
            }

            notificationList = new ArrayList<>();
            notificationList.clear();

            /**
             * Checking if products have expired
             */
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


            ((HomeActivity)getActivity()).setFragmentRefreshListener(new HomeActivity.FragmentRefreshListener() {
                @Override
                public void onRefresh(int id) {
                    int i =0;
                    for (Product product:
                         filterProductList) {

                        if(product.getProductId() == id) {
                            break;
                        }
                        i++;
                    }

                    productAdapter.remove(productAdapter.getItem(i));
                    productAdapter.setNotifyOnChange(true);
                    productAdapter.notifyDataSetChanged();
                }
            });
            /**
             * Calling a detail box, if user wants to modify the state
             */
            productListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                    final int viewPosition = position;
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("What to do with " + productAdapter.getItem(viewPosition).getProductName() + "?");
                    builder.setMessage("Please select action.");
                    builder.setPositiveButton("Consumed", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            productAdapter.getItem(viewPosition).setConsumed(true);
                            productAdapter.getItem(viewPosition).setExpired(false);
                            productAdapter.getItem(viewPosition).setStocked(false);
                            dbManager.updateProduct(productAdapter.getItem(viewPosition));

                            productAdapter.remove(productAdapter.getItem(viewPosition));

                            productAdapter.setNotifyOnChange(true);
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Expired", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            productAdapter.getItem(viewPosition).setExpired(true);
                            productAdapter.getItem(viewPosition).setConsumed(false);
                            productAdapter.getItem(viewPosition).setStocked(false);
                            dbManager.updateProduct(productAdapter.getItem(viewPosition));

                            productAdapter.remove(productAdapter.getItem(viewPosition));

                            productAdapter.setNotifyOnChange(true);
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                            deleteDialog.setTitle("Delete?");
                            deleteDialog.setMessage("Are you sure you want to delete?");
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
                        }
                    });
                    androidx.appcompat.app.AlertDialog alert = builder.create();
                    alert.show();
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
        // M/d (not MM/dd): the picker writes unpadded dates like "6/18/2026", which the
        // legacy SimpleDateFormat only accepted because of lenient parsing.
        DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy");
        try {
            LocalDate expiry = LocalDate.parse(productObj.getExpiryDate(), format);
            // Legacy semantics (Joda truncation toward zero, then +1): future dates
            // count as plain calendar-day difference, but the expiry date itself is
            // "1 day left" — items flip to expired only the day after expiry.
            int diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), expiry);
            return diff == 0 ? 1 : diff;
        } catch (DateTimeParseException e) {
            // Unparseable dates must not destructively mark items expired.
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Creates notification when called!
     */
    public void createNotification() {
        if (notificationList.isEmpty()) {
            return;
        }
        // areNotificationsEnabled() is correct on every API level; checking the
        // POST_NOTIFICATIONS permission directly reports DENIED on API < 33 where
        // the permission does not exist.
        if (!NotificationManagerCompat.from(myContext).areNotificationsEnabled()) {
            return;
        }

        Intent intent = new Intent(myContext, HomeActivity.class);
        Integer[] simpleArray = new Integer[notificationList.size()];
        notificationList.toArray(simpleArray);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("List", simpleArray);
        PendingIntent pIntent = PendingIntent.getActivity(myContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(myContext, StockedApp.CHANNEL_EXPIRY)
                .setContentTitle("Stocked!")
                .setContentText(notificationList.size() + " Item(s) about to expire")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(myContext).notify(0, notification);
    }

}
