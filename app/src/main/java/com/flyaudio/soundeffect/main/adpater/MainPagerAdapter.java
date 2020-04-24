package com.flyaudio.soundeffect.main.adpater;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int page) {
        return fragments.get(page);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
