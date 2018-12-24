package com.anshulvyas.csc780.grocerymanagr;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

/**
 * ShoppingItemActivity is used to add the items that user wants to buy next time he/she is at a grocery store.
 * User is prompted to add the item name.
 */
public class ShoppingItemActivity extends AppCompatActivity {

    private FloatingActionButton mFAB3;
    EditText et_shoppingItemName;
    CoordinatorLayout coordinatorLayout;

    /**
     * Called when the activity is created for the first time
     * @param savedInstanceState
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.shoopingCoordinatorLayout);

        /**
         * Instantiate ToolBar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add new item");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        et_shoppingItemName = (EditText) findViewById(R.id.editText_shopping_item_name);

         /**
          * Instantiate Floating Action button
          */
        mFAB3 = (FloatingActionButton) findViewById(R.id.FAB_check_shop);
        mFAB3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DBManager db = new DBManager(ShoppingItemActivity.this);

                String shoppingItemName = et_shoppingItemName.getText().toString();
                if(shoppingItemName.matches("")) {
                    Snackbar.make(coordinatorLayout, "Please enter name of an item.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Product newProduct = new Product(shoppingItemName, "", "", true, false, false, true);

                //Flag Logic
//                newProduct.setExpired(true);
//                db.updateProduct(newProduct);

                db.saveProduct(newProduct);

                Intent intentHome = new Intent(ShoppingItemActivity.this, HomeActivity.class);
                intentHome.putExtra(Product.PRODUCT_KEY, 1);
                startActivity(intentHome);
                finish();
            }
        });

    }
}
