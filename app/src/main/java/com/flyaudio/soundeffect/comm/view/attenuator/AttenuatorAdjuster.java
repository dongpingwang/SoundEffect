package com.flyaudio.soundeffect.comm.view.attenuator;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.flyaudio.soundeffect.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.flyaudio.soundeffect.comm.view.attenuator.AttenuatorAdjuster.AttenuatorBtn.*;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */

public class AttenuatorAdjuster extends FrameLayout {

    private static final long CLICK_DELAY_MILLIS = 800;
    private static final long PRESS_DELAY_MILLIS = 50;

    private View[] buttons = new View[BTN_RIGHT + 1];


    @IntDef({BTN_CENTER, BTN_UP, BTN_DOWN, BTN_LEFT, BTN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AttenuatorBtn {
        int BTN_CENTER = 0;
        int BTN_UP = 1;
        int BTN_DOWN = 2;
        int BTN_LEFT = 3;
        int BTN_RIGHT = 4;
    }

    private long delayMillis;
    private Runnable touchRunnable;
    private OnAdjustTouchListener touchListener;

    public AttenuatorAdjuster(@NonNull Context context) {
        this(context, null);
    }

    public AttenuatorAdjuster(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AttenuatorAdjuster(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View contentView = inflate(context, R.layout.view_attenuator_button, this);

        buttons[BTN_CENTER] = contentView.findViewById(R.id.attenuator_adjuster_center);
        buttons[BTN_UP] = contentView.findViewById(R.id.attenuator_adjuster_up);
        buttons[BTN_DOWN] = contentView.findViewById(R.id.attenuator_adjuster_down);
        buttons[BTN_LEFT] = contentView.findViewById(R.id.attenuator_adjuster_left);
        buttons[BTN_RIGHT] = contentView.findViewById(R.id.attenuator_adjuster_right);

        touchRunnable = new Runnable() {
            @Override
            public void run() {
                View touchView = (View) getTag();
                if (touchView != null) {
                    if (touchListener != null) {
                        touchListener.onTouch((Integer) touchView.getTag());
                    }
                    postDelayed(touchRunnable, delayMillis);
                    if (delayMillis != PRESS_DELAY_MILLIS) {
                        delayMillis = PRESS_DELAY_MILLIS;
                    }
                }
            }
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setTag(i);
            buttons[i].setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return AttenuatorAdjuster.this.onTouch(v, event);
                }
            });
        }
    }

    private boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        long w = view.getWidth();
        long h = view.getHeight();
        int sizeLength = (int) (Math.sqrt(w * w + h * h) + 0.5f);

        if (action == MotionEvent.ACTION_DOWN) {
            delayMillis = CLICK_DELAY_MILLIS;
            setTag(view);
            removeCallbacks(touchRunnable);
            post(touchRunnable);
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_OUTSIDE
                || action == MotionEvent.ACTION_CANCEL
                || x < (w - sizeLength) / 2 || x > w + (sizeLength - w) / 2
                || y < (h - sizeLength) / 2 || y > h + (sizeLength - h) / 2) {
            delayMillis = CLICK_DELAY_MILLIS;
            removeCallbacks(touchRunnable);
            setTag(null);
        }
        return false;
    }

    public interface OnAdjustTouchListener {
        /**
         * 单击和长按
         *
         * @param button 上下左右居中按钮中的一个
         */
        void onTouch(@AttenuatorBtn int button);
    }

    public void setAdjustListener(OnAdjustTouchListener listener) {
        this.touchListener = listener;
    }

    public void displayIfBackRowOff(boolean on) {

    }
}
