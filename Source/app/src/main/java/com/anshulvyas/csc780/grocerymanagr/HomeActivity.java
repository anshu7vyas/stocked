package com.anshulvyas.csc780.grocerymanagr;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.anshulvyas.csc780.grocerymanagr.Adapters.PagerAdapter;
//mport com.anshulvyas.csc780.grocerymanagr.Model.CartProvider;
import com.anshulvyas.csc780.grocerymanagr.Model.DBOpenHelper;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private Product mProductObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        /*
        Instantiate ToolBar
         */
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        /*
        Tab layout
         */
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Home"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Shopping List"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Timeline"));
        mTabLayout.setTabTextColors(Color.WHITE, Color.BLACK);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /*
        ViewPager Adapter
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
//            mProductObj = getIntent().getExtras().getParcelable(Product.PRODUCT_KEY);
            int pagerNumber = getIntent().getExtras().getInt(Product.PRODUCT_KEY);
            mViewPager.setCurrentItem(pagerNumber);
            //setViewPager(pagerNumber);

        }

    }

//    public void setViewPager(int pagerNumber) {
//        if (pagerNumber == 0) {
//            try {
////                Log.i("~!@#$HOMEACTIVITY", mProductObj.toString());
////                Bundle bundle = new Bundle();
////                bundle.putParcelable(Product.PRODUCT_KEY, mProductObj);
//
//                HomeFragment homeObj = new HomeFragment();
//                //homeObj.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.pager, homeObj).commit();
//
////            ShoppingListFragment shoppingObj = new ShoppingListFragment();
////            shoppingObj.setArguments(bundle);
////            getSupportFragmentManager().beginTransaction().replace(R.id.pager, shoppingObj).commit();
//
//            } catch (Exception e) {
//                Log.i("~!@#$HOMEACTIVITY", e.getMessage());
//            }
//        } else if (pagerNumber == 1) {
//            try {
////                Log.i("~!@#$HOMEACTIVITY", mProductObj.toString());
////                Bundle bundle = new Bundle();
////                bundle.putParcelable(Product.PRODUCT_KEY, mProductObj);
//
//                ShoppingListFragment shoppingObj = new ShoppingListFragment();
//                //homeObj.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.pager, shoppingObj).commit();
//
////            ShoppingListFragment shoppingObj = new ShoppingListFragment();
////            shoppingObj.setArguments(bundle);
////            getSupportFragmentManager().beginTransaction().replace(R.id.pager, shoppingObj).commit();
//
//            } catch (Exception e) {
//                Log.i("~!@#$HOMEACTIVITY", e.getMessage());
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
}
