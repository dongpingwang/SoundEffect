package com.flyaudio.soundeffect.backup.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flyaudio.lib.adapter.Adapter;
import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.bean.Device;

import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/5/23  22:21
 * email wangdongping@flyaudio.cn
 */
public class DiskListAdapter extends RecyclerViewAdapter<Device> {


    public DiskListAdapter(@NonNull Context context, @Nullable List<Device> datas) {
        super(context, datas);
    }

    @Override
    public int getItemLayoutId(int i) {
        return R.layout.recyclerview_item_disk;
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder<Device> holder) {
        TextView tvDiskName = holder.getView(R.id.tv_disk_name);
        RadioButton rbCheck = holder.getView(R.id.rb_index);
        tvDiskName.setText(getData(holder.getItemPosition()).getDescription());
        rbCheck.setChecked(getData(holder.getItemPosition()).isChecked());
    }
}
