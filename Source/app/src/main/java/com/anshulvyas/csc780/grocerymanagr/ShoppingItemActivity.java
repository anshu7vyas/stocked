package com.anshulvyas.csc780.grocerymanagr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

/**
 * Created by av7 on 10/15/15.
 */
public class ShoppingItemActivity extends AppCompatActivity {

    private FloatingActionButton mFAB3;
    EditText et_shoppingItemName, et_shoppingCategory, et_shoppingExpiry, et_shoppingNotify;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        /*
        Instantiate ToolBar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add new item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        et_shoppingItemName = (EditText) findViewById(R.id.editText_shopping_item_name);

         /*
        Instantiate Floating Action button
         */
        mFAB3 = (FloatingActionButton) findViewById(R.id.FAB_check_shop);
        mFAB3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DBManager db = new DBManager(ShoppingItemActivity.this);

                String shoppingItemName = et_shoppingItemName.getText().toString();

                Product newProduct = new Product(shoppingItemName, 0, "", "", true, false, false,
                        true);

                db.saveProduct(newProduct);
//                List<Product> productList = db.getAllProducts();
//                for(int i = 0; i < productList.size(); i++) {
//                    Log.i("~!@#$", productList.get(i).toString());
//                }

                Intent intentHome = new Intent(ShoppingItemActivity.this, HomeActivity.class);
                intentHome.putExtra(Product.PRODUCT_KEY, 1);
                startActivity(intentHome);
                finish();
            }
        });


    }
}
