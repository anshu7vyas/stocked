package com.anshulvyas.csc780.grocerymanagr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by av7 on 10/15/15.
 */
public class HomeFragment extends Fragment {

    private FloatingActionButton mFAB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null ) {
            Product productObj = getArguments().getParcelable(Product.PRODUCT_KEY);

            try {
                Log.i("~!@#FRAGMENT", productObj.toString());
            } catch (Exception e) {
                Log.i("~!@#FRAGMENT", e.getMessage());
            }
        } else {
            Log.i("~!@#FRAGMENT","ARGS are null");
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);



        /*
        Instantiate Floating Action button
         */
        mFAB = (FloatingActionButton)  view.findViewById(R.id.addFAB);
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
