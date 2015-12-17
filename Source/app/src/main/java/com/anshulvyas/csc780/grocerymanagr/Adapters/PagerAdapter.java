package com.anshulvyas.csc780.grocerymanagr.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.anshulvyas.csc780.grocerymanagr.HomeFragment;
import com.anshulvyas.csc780.grocerymanagr.ShoppingListFragment;
import com.anshulvyas.csc780.grocerymanagr.TimelineFragment;


/**
 * PagerAdapter provides the means to switch between different
 * fragments in the HomeActivity.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumofTabs) {
        super(fm);
        this.mNumOfTabs = NumofTabs;
    }


    /**
     * Switches fragments according to the tab number.
     * @param position
     * @return required fragment
     */
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

    /**
     * Get int count of the number of tabs/fragments.
     * @return number of tabs
     */
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
