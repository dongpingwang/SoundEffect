package com.flyaudio.soundeffect.setting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flyaudio.lib.adapter.Adapter;
import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.soundeffect.R;

import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:34
 * email wangdongping@flyaudio.cn
 */
public class SettingListAdapter extends RecyclerViewAdapter<String> {

    public SettingListAdapter(@NonNull Context context, @Nullable List<String> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutId(int i) {
        return R.layout.recyclerview_item_setting;
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder<String> holder) {

    }
}
