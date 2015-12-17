package com.anshulvyas.csc780.grocerymanagr;


import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.anshulvyas.csc780.grocerymanagr.Adapters.PagerAdapter;

/**
 * The main activity which constitutes of all 3 fragments - HomeFragment, ShoppingListFragment, TimelineFragment
 */
public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private Product mProductObj;

    /**
     * Called when the activity is being created for the first time.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    /**
     * Called when activity start-up is complete
     * @param savedInstanceState
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
}
