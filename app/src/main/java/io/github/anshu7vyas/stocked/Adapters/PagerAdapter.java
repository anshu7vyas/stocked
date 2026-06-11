package io.github.anshu7vyas.stocked.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import io.github.anshu7vyas.stocked.HomeFragment;
import io.github.anshu7vyas.stocked.ShoppingListFragment;
import io.github.anshu7vyas.stocked.TimelineFragment;


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
