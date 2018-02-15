package com.wgt.locationtraveller.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wgt.locationtraveller.fragment.HomeFragment;
import com.wgt.locationtraveller.fragment.MessageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debasish on 12-02-2018.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private int count;

    //2nd logic
    private List<Fragment> fragList;

    public PagerAdapter(FragmentManager fm, int count, List<Fragment> fragList) {
        super(fm);
        this.count = count;
        this.fragList = fragList;
    }

    @Override
    public Fragment getItem(int position) {
        /*switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MessageFragment();
            default:
                return null;
        }*/
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

}
