package com.flyaudio.soundeffect.setting.fragment;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.lib.constant.TimeUnit;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.base.AbstractFragment;
import com.flyaudio.soundeffect.config.AppPreferences;
import com.flyaudio.soundeffect.setting.util.SettingUtils;
import com.flyaudio.soundeffect.setting.adapter.SettingListAdapter;
import com.flyaudio.soundeffect.test.activity.TestActivity;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class SettingFragment extends AbstractFragment implements RecyclerViewAdapter.OnItemClickListener {

    private long[] mHints = new long[5];
    private RecyclerView rvSettingList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onInit() {
        rvSettingList = getView(R.id.rv_setting_list);
        SettingListAdapter adapter = new SettingListAdapter(context(), SettingUtils.getNames());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context(), LinearLayoutManager.VERTICAL, false);
        rvSettingList.setLayoutManager(layoutManager);
        rvSettingList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        test();
    }

    @Override
    public void onItemClick(int position) {
        SettingUtils.startActivity(context(), position);
    }

    private void test() {
        if (!AppPreferences.USER_DEBUG) {
            return;
        }
        // 点击5次打开test模块
        getView(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
                mHints[mHints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - mHints[0] <= TimeUnit.SEC) {
                    startActivity(new Intent(context(), TestActivity.class));
                }
            }
        });
    }
}
