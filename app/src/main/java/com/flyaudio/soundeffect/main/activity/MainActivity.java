package com.flyaudio.soundeffect.main.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.flyaudio.lib.ui.flyaudio.FlyTabBar;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.base.AbstractActivity;
import com.flyaudio.soundeffect.equalizer.fragment.EqFragment;
import com.flyaudio.soundeffect.position.fragment.ListenPositionFragment;
import com.flyaudio.soundeffect.setting.fragment.SettingFragment;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class MainActivity extends AbstractActivity implements FlyTabBar.OnSelectedIndexChangedListener {

    private static final int FRAGMENT_EQ = 0;
    private static final int FRAGMENT_POSITION = 1;
    private static final int FRAGMENT_SETTING = 2;
    private static final String[] TAGS = new String[]{"FRAGMENT_EQ", "FRAGMENT_POSITION", "FRAGMENT_SETTING"};
    private static final String KEY_CURRENT_TAB_INDEX = "current_tab_index";
    private FlyTabBar tabBar;
    private Fragment[] fragments = new Fragment[TAGS.length];

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onInit() {
        tabBar = getView(R.id.tab_bar);
        tabBar.setOnSelectedIndexChangedListener(this);
        tabBar.setSelectedIndex(FRAGMENT_EQ);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_TAB_INDEX, tabBar.getSelectedIndex());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (tabBar != null) {
            tabBar.setSelectedIndex(savedInstanceState.getInt(KEY_CURRENT_TAB_INDEX, FRAGMENT_EQ));
        }
    }

    @Override
    public void onSelectedIndexChanged(int pre, int current) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(TAGS[current]);
        if (fragmentByTag != null && fragments[current] == null) {
            fragments[current] = fragmentByTag;
        }
        if (fragments[current] == null) {
            if (current == FRAGMENT_EQ) {
                fragments[current] = new EqFragment();
            } else if (current == FRAGMENT_POSITION) {
                fragments[current] = new ListenPositionFragment();
            } else {
                fragments[current] = new SettingFragment();
            }
            ft.add(R.id.fl_main, fragments[current], TAGS[current]);
        }
        if (pre >= 0 && fragments[pre] != null) {
            ft.hide(fragments[pre]);
        }
        ft.show(fragments[current]);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
