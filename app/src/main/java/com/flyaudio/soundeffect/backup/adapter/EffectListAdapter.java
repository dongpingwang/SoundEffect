package com.flyaudio.soundeffect.backup.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flyaudio.lib.adapter.Adapter;
import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.bean.EffectFile;

import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-5-25
 * email wangdongping@flyaudio.cn
 */
public class EffectListAdapter extends RecyclerViewAdapter<EffectFile> {

    public EffectListAdapter(@NonNull Context context, @Nullable List<EffectFile> datas) {
        super(context, datas);
    }

    @Override
    public int getItemLayoutId(int i) {
        return R.layout.recyclerview_item_effect_file;
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder<EffectFile> holder) {
        ((TextView) (holder.getView(R.id.tv_file_name))).setText(getData(holder.getItemPosition()).getName());
        ((TextView) (holder.getView(R.id.tv_file_path))).setText(getData(holder.getItemPosition()).getPath());
        ((RadioButton) (holder.getView(R.id.rb_file_index))).setChecked(getData(holder.getItemPosition()).isChecked());
    }

    public void updateSelect(int position) {
        if (position >= 0 && position < getDatas().size()) {
            for (int i = 0; i < getDatas().size(); i++) {
                getDatas().get(i).setChecked(false);
            }
            getData(position).setChecked(true);
            refreshAdapter();
        }
    }

    public EffectFile getCurrentEffect() {
        EffectFile file = null;
        if (getDatas() != null && !getDatas().isEmpty()) {
            for (EffectFile item : getDatas()) {
                if (item.isChecked()) {
                    file = item;
                }
            }
        }
        return file;
    }
}
