package com.anshulvyas.csc780.grocerymanagr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by av7 on 9/24/15.
 */
public class AddItemActivity extends AppCompatActivity {
    private FloatingActionButton mFAB2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        /*
        Instantiate ToolBar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Spinner spinner = (Spinner) findViewById(R.id.spinner);

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //        R.array.items_list, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);

        /*
        Instantiate Floating Action button
         */
        mFAB2 = (FloatingActionButton)  findViewById(R.id.FAB_check);
        mFAB2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
                //startActivity(intent);
                Snackbar.make(v, "Data to be added on home activity", Snackbar.LENGTH_LONG).show();
            }
        });



    }
}
