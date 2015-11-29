package com.anshulvyas.csc780.grocerymanagr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
//        if(getArguments() != null ) {
//            Product productObj = getArguments().getParcelable(Product.PRODUCT_KEY);
//
//            try {
//                Log.i("~!@#FRAGMENT", productObj.toString());
//            } catch (Exception e) {
//                Log.i("~!@#FRAGMENT", e.getMessage());
//            }
//        } else {
//            Log.i("~!@#FRAGMENT","ARGS are null");
//        }

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

            productListView = (ListView) view.findViewById(R.id.listView_home_product);
            final ProductAdapter productAdapter = new ProductAdapter(getActivity().getBaseContext(), R.layout.list_view_home,
                    filterProductList);
            productListView.setAdapter(productAdapter);

            productAdapter.setNotifyOnChange(true);
            productAdapter.notifyDataSetChanged();

            productListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    dbManager.deleteProduct(productAdapter.getItem(position));
                    Log.d("DEMO======>", "PRODUCT DELETED");
                    Toast.makeText(getActivity(), "ProductsItem deleted", Toast.LENGTH_LONG).show();

                    List<Product> productListDB = dbManager.getAllProducts();
                    Log.d("DEMO=====>", productListDB.toString());

                    final ProductAdapter productAdapter = new ProductAdapter(getActivity().getBaseContext(), R.layout.list_view_home,
                            productListDB);
                    productListView.setAdapter(productAdapter);

                    productAdapter.setNotifyOnChange(true);
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
