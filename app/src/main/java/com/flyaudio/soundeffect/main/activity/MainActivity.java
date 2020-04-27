package com.flyaudio.soundeffect.main.activity;

import android.support.v4.app.Fragment;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.ui.flyaudio.FlyTabBar;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.NoScrollViewPager;
import com.flyaudio.soundeffect.equalizer.fragment.EqFragment;
import com.flyaudio.soundeffect.main.adpater.MainPagerAdapter;
import com.flyaudio.soundeffect.position.fragment.ListenPositionFragment;
import com.flyaudio.soundeffect.setting.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class MainActivity extends BaseActivity {

    private FlyTabBar tabBar;
    private NoScrollViewPager pagerMain;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initTabs();
        initViewPages();
    }


    private void initTabs() {
        tabBar = getView(R.id.tab_bar);
        tabBar.setOnSelectedIndexChangedListener(new FlyTabBar.OnSelectedIndexChangedListener() {
            @Override
            public void onSelectedIndexChanged(int pre, int current) {
                pagerMain.setCurrentItem(current, false);
            }
        });
    }

    private void initViewPages() {
        pagerMain = getView(R.id.vp_main);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new EqFragment());
        fragments.add(new ListenPositionFragment());
        fragments.add(new SettingFragment());
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        pagerMain.setAdapter(adapter);
        tabBar.setSelectedIndex(0);
    }
}
