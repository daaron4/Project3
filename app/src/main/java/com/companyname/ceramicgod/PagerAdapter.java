package com.companyname.ceramicgod;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by RandyBiglow on 6/1/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mTabs;

    public PagerAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new NearbyFragment();
            case 1:
                return new PinnableMapFragment();
            case 2:
                return new FavoritesFragment();
            case 3:
                return new RandomPhotoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabs;
    }
}
