package com.companyname.ceramicgod;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mTabs;
    private Fragment currentFragment;

    public PagerAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                currentFragment = new NearbyFragment();
                return currentFragment;
            case 1:
                currentFragment = new NewReviewFragment();
                return currentFragment;
            case 5:
                currentFragment = new ToiletFragment();
                return currentFragment;
            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        currentFragment = null;
        super.destroyItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public int getCount() {
        return mTabs;
    }
}
