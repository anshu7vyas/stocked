package com.anshulvyas.csc780.grocerymanagr.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anshulvyas.csc780.grocerymanagr.HomeFragment;
import com.anshulvyas.csc780.grocerymanagr.ShoppingListFragment;
import com.anshulvyas.csc780.grocerymanagr.TimelineFragment;

/**
 * Created by av7 on 10/15/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumofTabs) {
        super(fm);
        this.mNumOfTabs = NumofTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HomeFragment mHomeTab = new HomeFragment();
                return mHomeTab;
            case 1:
                ShoppingListFragment mShoppingTab = new ShoppingListFragment();
                return mShoppingTab;
            case 2:
                TimelineFragment mTimelineTab = new TimelineFragment();
                return mTimelineTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
