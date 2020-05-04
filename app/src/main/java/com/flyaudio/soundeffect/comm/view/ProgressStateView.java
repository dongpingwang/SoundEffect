package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.graphics.drawable.RotateDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/5/4  18:59
 * email wangdongping@flyaudio.cn
 */
public class ProgressStateView extends FrameLayout {

    private ProgressBar progressBar;
    private TextView tvResult;

    public ProgressStateView(@NonNull Context context) {
        this(context, null);
    }

    public ProgressStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_progress_state, this);
        progressBar = (ProgressBar) findViewById(R.id.pb_state);
        tvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    public void updateInComplete(String completeHint) {
        progressBar.setVisibility(GONE);
        tvResult.setText(completeHint);
    }

    public void updateInProgress(String progressHint) {
        progressBar.setVisibility(VISIBLE);
        tvResult.setText(progressHint);
    }
}
