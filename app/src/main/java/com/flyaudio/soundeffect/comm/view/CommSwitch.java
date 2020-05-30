package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/5/2  15:29
 * email wangdongping@flyaudio.cn
 */
public class CommSwitch extends FrameLayout {

    private TextView tvName;
    private Switch sw;


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
    }

    public void setChecked(boolean checked) {
        sw.setChecked(checked);
    }

    public boolean isChecked() {
        return sw.isChecked();
    }
}
