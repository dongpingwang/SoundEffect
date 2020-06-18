package com.flyaudio.soundeffect.setting.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.base.AbstractFragment;
import com.flyaudio.soundeffect.setting.util.SettingUtils;
import com.flyaudio.soundeffect.setting.adapter.SettingListAdapter;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class SettingFragment extends AbstractFragment implements RecyclerViewAdapter.OnItemClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onInit() {
        RecyclerView rvSettingList = getView(R.id.rv_setting_list);
        SettingListAdapter adapter = new SettingListAdapter(context(), SettingUtils.getNames());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context(), LinearLayoutManager.VERTICAL, false);
        rvSettingList.setLayoutManager(layoutManager);
        rvSettingList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        SettingUtils.test(context(), getView(R.id.tv_test));
    }

    @Override
    public void onItemClick(int position) {
        SettingUtils.startActivity(context(), position);
    }

}
