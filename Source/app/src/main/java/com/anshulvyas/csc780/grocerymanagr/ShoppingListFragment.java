package com.anshulvyas.csc780.grocerymanagr;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anshulvyas.csc780.grocerymanagr.AppDatabase.CartProvider;
import com.anshulvyas.csc780.grocerymanagr.AppDatabase.DBOpenHelper;

/**
 * Created by av7 on 10/15/15.
 */
public class ShoppingListFragment extends Fragment {

    private FloatingActionButton mFAB1;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);


        /*
        Instantiate Floating Action button
         */
        mFAB1 = (FloatingActionButton)  view.findViewById(R.id.itemFAB);
        mFAB1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextActivity();
                //Snackbar.make(v, "Hello Snackbar", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void nextActivity() {
        Intent intent = new Intent(getActivity(), ShoppingItemActivity.class);
        startActivity(intent);
    }
}


