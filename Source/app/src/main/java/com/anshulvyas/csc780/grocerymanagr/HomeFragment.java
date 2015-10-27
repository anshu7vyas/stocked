package com.anshulvyas.csc780.grocerymanagr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by av7 on 10/15/15.
 */
public class HomeFragment extends Fragment {

    private FloatingActionButton mFAB;

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
                //Snackbar.make(v, "Hello Snackbar", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void nextActivity() {
        Intent intent = new Intent(getActivity(), AddItemActivity.class);
        startActivity(intent);
    }

}
