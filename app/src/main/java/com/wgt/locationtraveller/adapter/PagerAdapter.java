package com.wgt.locationtraveller.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wgt.locationtraveller.fragment.HomeFragment;
import com.wgt.locationtraveller.fragment.MessageFragment;

/**
 * Created by debasish on 12-02-2018.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private int count;

    public PagerAdapter(FragmentManager fm, int count) {
        super(fm);
        this.count = count;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MessageFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

}
