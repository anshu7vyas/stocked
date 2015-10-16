package com.anshulvyas.csc780.grocerymanagr;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anshulvyas.csc780.grocerymanagr.Adapters.PagerAdapter;
import com.anshulvyas.csc780.grocerymanagr.AppDatabase.CartProvider;
import com.anshulvyas.csc780.grocerymanagr.AppDatabase.DBOpenHelper;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    //private FloatingActionButton mFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        insertItem("New Item");

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


        /*
        Instantiate Floating Action button
         */
        //mFAB = (FloatingActionButton)  findViewById(R.id.FAB);
        //mFAB.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
        //        startActivity(intent);
                //Snackbar.make(v, "Hello Snackbar", Snackbar.LENGTH_LONG).show();
        //    }
        //});
    }

    private void insertItem(String itemText) {

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.CART_ITEM_TEXT, itemText);
        Uri cartUri = getContentResolver().insert(CartProvider.CONTENT_URI, values);
        Log.d("HomeActivity", "Inserted item " + cartUri.getLastPathSegment());
    }

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
