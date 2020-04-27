package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/4/27  21:42
 * email wangdongping@flyaudio.cn
 */
public class CommTitleBar extends FrameLayout implements View.OnClickListener {

    private TitleBarActionListener listener;
    private TextView tvName;

    public CommTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public CommTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_comm_title_bar, this);
        tvName = (TextView) findViewById(R.id.tv_title_name);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommTitleBar);
        String title = typedArray.getString(R.styleable.CommTitleBar_title_name);
        (tvName).setText(title);
        typedArray.recycle();

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_reset).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            if (view.getId() == R.id.iv_back) {
                listener.onBack();
            } else if (view.getId() == R.id.tv_reset) {
                listener.onReset();
            }
        }

    }

    public void setTitleName(String titleName) {
        tvName.setText(titleName);
    }

    public void setListener(TitleBarActionListener listener) {
        this.listener = listener;
    }

    public interface TitleBarActionListener {
        /**
         * 返回
         */
        void onBack();

        /**
         * 重置
         */
        void onReset();
    }
}
