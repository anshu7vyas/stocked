package com.anshulvyas.csc780.grocerymanagr;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import java.util.Calendar;

/**
 * AddItemActivity is used to add the items that user wants to keep track of.
 * User is prompted to add the category, name and expiry date of the item.
 */
public class AddItemActivity extends AppCompatActivity {
    private FloatingActionButton mFAB2;

    Spinner spinnerCategory;
    EditText et_itemName, et_expiry, et_notify;
    final static boolean SHOPPING_CHECK = false;
    TextView tV_productExpiry;
    Calendar expiryDate, currentDate;
    CoordinatorLayout coordinatorLayout;

    /**
     * Called when the activity is created for the first time
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

       coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        /**
         * Instantiate ToolBar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add Item");

        spinnerCategory = (Spinner) findViewById(R.id.spinner);
        et_itemName = (EditText) findViewById(R.id.editText_itemName);
        tV_productExpiry = (TextView) findViewById(R.id.textView_datePicker_expiry);


        currentDate = Calendar.getInstance();

        /**
         * to add a datePicker dialog on clicking the textView for ProductExpiry.
         */
        tV_productExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        tV_productExpiry.setText(1 + selectedMonth + "/" + selectedDay + "/" + selectedYear);
                        expiryDate = Calendar.getInstance();
                        expiryDate.set(Calendar.DAY_OF_MONTH, selectedDay);
                        expiryDate.set(Calendar.MONTH, selectedMonth);
                        expiryDate.set(Calendar.YEAR, selectedYear);
                        Log.i("~!@#", expiryDate.toString());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();

                /**
                 * setting the minimum date to tomorrow, as no one would like to enter items that expire today only!
                 */
                DatePicker dp = mDatePicker.getDatePicker();
                dp.setMinDate((mCurrentDate.getTimeInMillis() - 1000) + 24 * 60 * 60 * 1000);
            }
        });

        /**
         * Instantiate Floating Action button
         */
        mFAB2 = (FloatingActionButton) findViewById(R.id.FAB_check);
        mFAB2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DBManager db = new DBManager(AddItemActivity.this);

                String category = spinnerCategory.getSelectedItem().toString();
                String productName = et_itemName.getText().toString();
                if (productName.matches("")) {
                    Snackbar.make(coordinatorLayout, "Please enter name of an item.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String productExpiry = tV_productExpiry.getText().toString();

                Product newProduct = new Product(productName, category, productExpiry, true, false, false, SHOPPING_CHECK);

                /**
                 * Inserting the entered product item into database
                 */
                db.saveProduct(newProduct);

                Intent intentHome = new Intent(AddItemActivity.this, HomeActivity.class);
                intentHome.putExtra(Product.PRODUCT_KEY, 0);
                startActivity(intentHome);
                finish();
            }
        });
    }

}
