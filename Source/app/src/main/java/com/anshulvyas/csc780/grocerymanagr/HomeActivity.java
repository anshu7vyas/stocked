package com.anshulvyas.csc780.grocerymanagr;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFAB;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*
        Instantiate ToolBar
         */
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
         * Obtain out DrawerLayout */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerRoot);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolBar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView nv = (NavigationView) findViewById(R.id.navView);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                String txt;
                switch (menuItem.getItemId()) {
                    case R.id.item_1:
                        txt = "Home Selected";
                        break;
                    case R.id.item_2:
                        txt = "Items to buy Selected";
                        break;
                    case R.id.item_3:
                        txt = "Timeline Selected";
                        break;
                    case R.id.item_4:
                        txt = "Settings Selected";
                        break;
                    case R.id.item_5:
                        txt = "Logout Selected";
                        break;
                    default:
                        txt = "Invalid Item Selected";
                }
                Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });


        /*
        Instantiate Floating Action button
         */
        mFAB= (FloatingActionButton)  findViewById(R.id.FAB);
        mFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
                startActivity(intent);
                //Snackbar.make(v, "Hello Snackbar", Snackbar.LENGTH_LONG).show();
            }
        });
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
        mDrawerToggle.syncState();
    }
}
