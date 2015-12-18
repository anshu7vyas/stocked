package com.anshulvyas.csc780.grocerymanagr;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.anshulvyas.csc780.grocerymanagr.Adapters.PagerAdapter;
import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

/**
 * The main activity which constitutes of all 3 fragments - HomeFragment, ShoppingListFragment, TimelineFragment
 */
public class HomeActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private DBManager dbManager;

    Bundle savedState;

    /**
     * Called when the activity is being created for the first time.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedState = savedInstanceState;
        setContentView(R.layout.activity_home);

        /**
         * Instantiate ToolBar
         */
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        /**
         * Tab layout
         */
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Home"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Shopping List"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Timeline"));
        mTabLayout.setTabTextColors(Color.WHITE, Color.BLACK);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /**
         * ViewPager Adapter
         */
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (getIntent().getExtras() != null) {
            int pagerNumber = getIntent().getExtras().getInt(Product.PRODUCT_KEY);
            mViewPager.setCurrentItem(pagerNumber);
        }
    }

    // TODO: 12/18/15
    boolean alreadyHandled = false;

    /**
     * Called when activity start-up is complete
     * @param savedInstanceState
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(!alreadyHandled) {
            notificationHandler();
            alreadyHandled=true;
        }
    }

    /**
     * Creates notification dialog box for each individual item that has expired.
     * @param productObj
     */
    public void createNotificationDialog(final Integer productObj) {
        final Product p = dbManager.get(productObj);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("What to do with " + p.getProductName() + "?");
        builder.setMessage("Please select action.");
        builder.setPositiveButton("Consumed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                p.setConsumed(true);
                p.setExpired(false);
                p.setStocked(false);
                dbManager.updateProduct(p);
            }
        });
        builder.setNegativeButton("Expired", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                p.setExpired(true);
                p.setConsumed(false);
                p.setStocked(false);
                dbManager.updateProduct(p);
            }
        });
        builder.setNeutralButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.setOnDismissListener(this);
        alert.show();
    }

    /**
     * Calls and handles createNotificationDialog()
     */
    public void notificationHandler() {
        Log.d("~!@#NOTIFICATIONREC", "inside onCreate()");

        dbManager = new DBManager(this);

        Intent intent = getIntent();

        if( intent.getExtras() != null ) {
            if(intent.hasExtra("List")){
                Integer[] arr = (Integer[]) intent.getExtras().get("List");
                for (Integer item : arr) {
                    Log.d("~!@#Array", String.valueOf(item));
                    createNotificationDialog(item);
                }
            }
        }
    }

    // TODO: 12/18/15
    @Override
    public void onDismiss(DialogInterface dialog) {
        //recreate();
    }
}
