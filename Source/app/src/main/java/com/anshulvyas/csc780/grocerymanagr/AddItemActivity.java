package com.anshulvyas.csc780.grocerymanagr;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import java.util.List;

/**
 * Created by av7 on 9/24/15.
 */
public class AddItemActivity extends AppCompatActivity {
    private FloatingActionButton mFAB2;

    Spinner spinnerCategory;

    EditText et_itemName, et_expiry, et_notify;
    final static boolean SHOPPING_CHECK = false;


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

        spinnerCategory = (Spinner) findViewById(R.id.spinner);
        et_itemName = (EditText) findViewById(R.id.editText_itemName);
        et_expiry = (EditText) findViewById(R.id.editText_expiry);
        et_notify = (EditText) findViewById(R.id.editText_notify);


        /*
        Instantiate Floating Action button
         */
        mFAB2 = (FloatingActionButton) findViewById(R.id.FAB_check);
        mFAB2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
                //startActivity(intent);
                //Snackbar.make(v, "Data to be added on home activity", Snackbar.LENGTH_LONG).show();
                DBManager db = new DBManager(AddItemActivity.this);

                String category = spinnerCategory.getSelectedItem().toString();
                String productName = et_itemName.getText().toString();
                String productExpiry = et_expiry.getText().toString();
                Integer productNotify = Integer.parseInt(et_notify.getText().toString());

                Product newProduct = new Product(productName, productNotify, category, productExpiry, true, false, false, SHOPPING_CHECK);

                db.saveProduct(newProduct);
//                List<Product> productList = db.getAllProducts();
//                for(int i = 0; i < productList.size(); i++) {
//                    Log.i("~!@#$", productList.get(i).toString());
//                }

                Intent intentHome = new Intent(AddItemActivity.this, HomeActivity.class);
                intentHome.putExtra(Product.PRODUCT_KEY, 0);
                startActivity(intentHome);
                finish();
            }
        });



    }
}
