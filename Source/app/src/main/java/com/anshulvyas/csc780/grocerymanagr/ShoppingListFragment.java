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
import android.widget.ListView;
import android.widget.Toast;

import com.anshulvyas.csc780.grocerymanagr.Adapters.ProductAdapter;
import com.anshulvyas.csc780.grocerymanagr.Adapters.ShoppingAdapter;
import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by av7 on 10/15/15.
 */
public class ShoppingListFragment extends Fragment {

    private FloatingActionButton mFAB1;
    private List<Product> shoppingList, filterShoppingList;
    private DBManager dbManager;
    private ListView shoppingListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DBManager(getActivity());
    }

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

            shoppingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    dbManager.deleteProduct(shoppingAdapter.getItem(position));
                    Log.d("DEMO======>", "PRODUCT DELETED");
                    Toast.makeText(getActivity(), "ProductsItem deleted", Toast.LENGTH_LONG).show();

                    //adapter.remove(adapter.getItem(position));
                    List<Product> shoppingListDB = dbManager.getAllProducts();
                    Log.d("DEMO=====>", shoppingListDB.toString());

                    final ShoppingAdapter shoppingAdapter = new ShoppingAdapter(getActivity().getBaseContext(), R.layout
                            .list_view_shopping,
                            shoppingListDB);
                    shoppingListView.setAdapter(shoppingAdapter);

                    shoppingAdapter.setNotifyOnChange(true);
                    return false;
                }
            });


        } else {
            Log.i("~!@#SHOPPINGFRAGMENT", "list view not displayed");
        }


        /*
        Instantiate Floating Action button
         */
        mFAB1 = (FloatingActionButton)  view.findViewById(R.id.itemFAB);
        mFAB1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextActivity();
            }
        });

        return view;
    }

    private void nextActivity() {
        Intent intent = new Intent(getActivity(), ShoppingItemActivity.class);
        startActivity(intent);
    }
}


