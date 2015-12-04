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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.anshulvyas.csc780.grocerymanagr.Adapters.ProductAdapter;
import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by av7 on 10/15/15.
 */
public class HomeFragment extends Fragment {

    private FloatingActionButton mFAB;
    private List<Product> productList, filterProductList;
    private DBManager dbManager;
    private ListView productListView;

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


            //TODO
            // FLAG LOGIC!!!
            //call joda daysbetween here. if 0, flag == true;


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
//                            Toast.makeText(getActivity(), productAdapter.getItem(viewPosition).getProductName() + " deleted", Toast
//                                    .LENGTH_LONG).show();
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

}
