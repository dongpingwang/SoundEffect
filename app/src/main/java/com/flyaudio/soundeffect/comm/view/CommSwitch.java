package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/5/2  15:29
 * email wangdongping@flyaudio.cn
 */
public class CommSwitch extends FrameLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tvName;
    private Switch sw;

    private OnItemClickListener listener;

    public CommSwitch(@NonNull Context context) {
        this(context, null);
    }

    public CommSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_comm_switch, this);
        tvName = (TextView) findViewById(R.id.tv_item_name);
        sw = (Switch) findViewById(R.id.sw_open);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommSwitch);
        String title = typedArray.getString(R.styleable.CommSwitch_switch_name);
        tvName.setText(title);
        typedArray.recycle();
        setOnClickListener(this);
        sw.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == this) {
            sw.setChecked(!sw.isChecked());
            if (listener != null) {
                listener.onCheckedChanged(sw.isChecked());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (listener != null) {
            listener.onCheckedChanged(b);
        }
    }

    public void setChecked(boolean checked) {
        sw.setChecked(checked);
    }

    public interface OnItemClickListener {
        /**
         * 点击或者Switch状态改变
         *
         * @param checked 当前的选中状态
         */
        void onCheckedChanged(boolean checked);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
