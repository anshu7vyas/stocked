package com.anshulvyas.csc780.grocerymanagr;


import android.app.AlertDialog;
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
import android.widget.ListView;

import com.anshulvyas.csc780.grocerymanagr.Adapters.ShoppingAdapter;
import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Hosts the listView of items that user wants to buy the next time he/she visits a grocery store.
 */
public class ShoppingListFragment extends Fragment {

    private FloatingActionButton mFAB1;
    private List<Product> shoppingList, filterShoppingList;
    private DBManager dbManager;
    private ListView shoppingListView;

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
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        shoppingList = dbManager.getAllProducts();
        filterShoppingList = new ArrayList<>();
        if(shoppingList.size() > 0){
            Log.i("~!@#SHOPPINGFRAGMENT", shoppingList.get(0).toString());

            for (int i = 0; i < shoppingList.size(); i++) {
                if (shoppingList.get(i).isShoppingCheck()) {
                    filterShoppingList.add(shoppingList.get(i));
                }
            }

            shoppingListView = (ListView) view.findViewById(R.id.listView_shopping_product);
            final ShoppingAdapter shoppingAdapter = new ShoppingAdapter(getActivity().getBaseContext(), R.layout
                    .list_view_shopping,
                    filterShoppingList);
            shoppingListView.setAdapter(shoppingAdapter);

            shoppingAdapter.setNotifyOnChange(true);
            shoppingAdapter.notifyDataSetChanged();

            /**
             * Calling a delete dialog box to confirm user's action before deleting the item from the Adapter.
             */
            shoppingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                    final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                    deleteDialog.setTitle("Delete?");
                    deleteDialog.setMessage("Are you sure you want to delete?");
                    final int viewPosition = position;

                    deleteDialog.setPositiveButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbManager.deleteProduct(shoppingAdapter.getItem(viewPosition));
                            Log.d("DEMO======>", "PRODUCT DELETED");
                            Snackbar.make(view, shoppingAdapter.getItem(viewPosition).getProductName() + " deleted", Snackbar
                                    .LENGTH_LONG).show();

                            shoppingAdapter.remove(shoppingAdapter.getItem(viewPosition));

                            shoppingAdapter.setNotifyOnChange(true);
                            shoppingAdapter.notifyDataSetChanged();
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

        } else {
            Log.i("~!@#SHOPPINGFRAGMENT", "list view not displayed");
        }

        /**
         * Instantiate Floating Action button
         */
        mFAB1 = (FloatingActionButton)  view.findViewById(R.id.itemFAB);
        mFAB1.setOnClickListener(new View.OnClickListener() {
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
        Intent intent = new Intent(getActivity(), ShoppingItemActivity.class);
        startActivity(intent);
    }
}


