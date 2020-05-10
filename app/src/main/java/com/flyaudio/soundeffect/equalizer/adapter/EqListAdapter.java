package com.flyaudio.soundeffect.equalizer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyaudio.lib.adapter.Adapter;
import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.config.EffectCommUtils;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;

import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class EqListAdapter extends RecyclerViewAdapter<EqMode> implements
        RecyclerViewAdapter.OnItemClickListener, View.OnClickListener {

    private static final int PRESET_SIZE = EffectCommUtils.EQ_PRESET_COUNT;
    private static final int EQ_MODE_MAX_COUNT = 30;

    private static final int MODE_ITEM = 0;
    private static final int CREATE_MODE = 1;

    private OnItemListener listener;

    private int current = -1;

    public EqListAdapter(@NonNull Context context, @Nullable List<EqMode> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutId(int i) {
        return R.layout.recyclerview_item_eq;
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder<EqMode> holder) {
        TextView tvName = holder.getView(R.id.tv_name);
        ImageView ivAdd = holder.getView(R.id.iv_add);
        ImageView ivEdit = holder.getView(R.id.iv_edit);

        int position = holder.getItemPosition();
        boolean modeItem = getItemType(position) == MODE_ITEM;
        tvName.setVisibility(modeItem ? View.VISIBLE : View.GONE);
        ivEdit.setVisibility(modeItem ? View.VISIBLE : View.GONE);
        ivAdd.setVisibility(modeItem ? View.GONE : View.VISIBLE);
        ivEdit.setVisibility(modeItem && position >= PRESET_SIZE ? View.VISIBLE : View.GONE);

        holder.getContentView().setBackground(ResUtils.getDrawable(R.drawable.bg_eq_item));
        if (modeItem && position < getDatas().size()) {
            tvName.setText(getData(position).getName());
            if (current >= 0 && current < getDatas().size() && current == position) {
                holder.getContentView().setBackground(ResUtils.getDrawable(R.drawable.bg_eq_item_select));
            }
        }

        setOnItemClickListener(this);
        ivEdit.setTag(position);
        ivEdit.setOnClickListener(this);
    }

    @Override
    public int getItemViewCount() {
        int count = super.getItemViewCount();
        return count < EQ_MODE_MAX_COUNT ? count + 1 : count;
    }


    @Override
    public int getItemType(int position) {
        return (getDatas().size() < EQ_MODE_MAX_COUNT && position == getItemViewCount() - 1) ? CREATE_MODE : MODE_ITEM;
    }

    @Override
    public void onItemClick(int position) {
        if (getItemType(position) == MODE_ITEM) {
            if (listener != null) {
                listener.onItemClick(position);
            }
        } else {
            if (listener != null) {
                listener.onCreateMode();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_edit && v.getTag() instanceof Integer) {
            listener.onItemEdit((int) v.getTag());
        }
    }

    public void updateChecked(int position) {
        int temp = current;
        current = position;
        notifyItemChanged(temp);
        notifyItemChanged(current);
    }

    public void updateCheckedById(int id) {
        int position = 0;
        for (int i = 0; i < getDatas().size(); i++) {
            if (id == getData(i).getId()) {
                position = i;
                break;
            }
        }
        updateChecked(position);
    }

    public EqMode getChecked() {
        return getData(current);
    }

    public void addEqMode(EqMode mode) {
        // 添加到倒数第二个
        addItem(getItemViewCount() - 1, mode);
        // 注意一定要添加这一行代码，如果当添加最后一个时，需要手动刷新最后一个条目，否则会报Inconsistency detected
        // https://www.jianshu.com/p/2eca433869e9
        if (getDatas().size() == EQ_MODE_MAX_COUNT) {
            notifyItemRangeChanged(getItemCount() - 1, getItemCount());
        }
        clearChecked();
    }

    public void deleteEqMode(int position) {
        removeItem(position);
        clearChecked();
        notifyItemChanged(position, getItemViewCount() - 1);
    }


    public int getCheckedPos() {
        EqMode checked = getChecked();
        if (checked != null) {
            return checked.getId();
        }
        return -1;
    }

    public void clearChecked() {
        int temp = current;
        current = -1;
        notifyItemChanged(temp);
    }

    public void setOnItemListener(OnItemListener listener) {
        this.listener = listener;
    }

    public interface OnItemListener {
        /**
         * 点击选中条目
         *
         * @param position
         */
        void onItemClick(int position);

        /**
         * 编辑条目
         *
         * @param position
         */
        void onItemEdit(int position);

        /**
         * 新建条目
         */
        void onCreateMode();
    }
}
