package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:21
 * email wangdongping@flyaudio.cn
 */
public class CommVerticalAdjustButton extends FrameLayout implements View.OnClickListener {

    private TextView tvValue;
    private AdjustListener listener;


    public CommVerticalAdjustButton(@NonNull Context context) {
        this(context, null);
    }

    public CommVerticalAdjustButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommVerticalAdjustButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_vertical_adjust_button, this);
        tvValue = (TextView) findViewById(R.id.tv_value);
        findViewById(R.id.btn_up).setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
    }

    public void setValue(String value) {
        tvValue.setText(value);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            if (v.getId() == R.id.btn_up) {
                listener.onUp();
            } else if (v.getId() == R.id.btn_down) {
                listener.onDown();
            }
        }
    }

    public void setListener(AdjustListener listener) {
        this.listener = listener;
    }

    public interface AdjustListener {
        /**
         * 调大
         */
        void onUp();

        /**
         * 调小
         */
        void onDown();
    }
}
