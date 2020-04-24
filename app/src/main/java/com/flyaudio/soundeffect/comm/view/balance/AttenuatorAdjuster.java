package com.flyaudio.soundeffect.comm.view.balance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.util.SkinUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Attenuator：衰减器
 */

public class AttenuatorAdjuster extends FrameLayout {

    public static final int BTN_CENTER = 0;
    public static final int BTN_UP = 1;
    public static final int BTN_DOWN = 2;
    public static final int BTN_LEFT = 3;
    public static final int BTN_RIGHT = 4;
    private View upBtn;
    private View downBtn;
    private View leftBtn;
    private View rightBtn;
    private View centerBtn;
    @Deprecated  private View[] btns = new View[5];

    @IntDef({BTN_CENTER, BTN_UP, BTN_DOWN, BTN_LEFT, BTN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AttenuatorBtn {
    }

    private OnClickListener clickListener;
    private BtnClickListener btnClickListener;

    private OnTouchListener touchListener;
    private BtnTouchListener btnTouchListener;

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
        View contentView = inflate(context, R.layout.sound_field_custom_position_layout, this);
        btns[BTN_CENTER] = centerBtn = contentView.findViewById(R.id.position_middle_btn);
        btns[BTN_UP] = upBtn = contentView.findViewById(R.id.position_up_btn);
        btns[BTN_DOWN] = downBtn = contentView.findViewById(R.id.position_down_btn);
        btns[BTN_LEFT] = leftBtn = contentView.findViewById(R.id.position_left_btn);
        btns[BTN_RIGHT] = rightBtn = contentView.findViewById(R.id.position_right_btn);

        centerBtn.setTag(BTN_CENTER);
        upBtn.setTag(BTN_UP);
        downBtn.setTag(BTN_DOWN);
        leftBtn.setTag(BTN_LEFT);
        rightBtn.setTag(BTN_RIGHT);
        for (View btn:btns) {
            SkinUtils.setBackgroundEffect(R.integer.bg_attenuator_btn, btn,R.drawable.bg_listen_position_btn,R.drawable.sf_attenuator_btn_bg_pic);
        }

        clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnClickListener != null)
                    btnClickListener.onBtnClick((int) v.getTag());
            }
        };
        upBtn.setClickable(true);
        upBtn.setOnClickListener(clickListener);
        downBtn.setOnClickListener(clickListener);
        leftBtn.setOnClickListener(clickListener);
        rightBtn.setOnClickListener(clickListener);
        centerBtn.setOnClickListener(clickListener);

        touchListener = new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateTouch((Integer) v.getTag());
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    stopTouch();
                }
                return false;//不拦截点击事件
            }
        };

        upBtn.setOnTouchListener(touchListener);
        downBtn.setOnTouchListener(touchListener);
        leftBtn.setOnTouchListener(touchListener);
        rightBtn.setOnTouchListener(touchListener);
        centerBtn.setOnTouchListener(touchListener);

    }

    private ScheduledExecutorService scheduledExecutor;//= Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> task;

    private void updateTouch(@AttenuatorBtn final int btn) {
        stopTouch();
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        task = scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = btn;
                handler.sendMessage(msg);
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void stopTouch() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            btnTouchListener.onBtnClick(msg.what);
            return false;
        }
    });

    public void setBtnClickListener(BtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public interface BtnClickListener {
        void onBtnClick(@AttenuatorBtn int btn);
    }

    public interface BtnTouchListener {
        void onBtnClick(@AttenuatorBtn int btn);
    }

    public void setBtnTouchListener(BtnTouchListener btnTouchListener) {
        this.btnTouchListener = btnTouchListener;
    }


    public void horizontalAdjust(boolean horizontal) {
        upBtn.setVisibility(horizontal ? VISIBLE :GONE );
        downBtn.setVisibility(horizontal ? VISIBLE : GONE);
        invalidate();
    }

    public void updateEnable(@AttenuatorBtn int btn , boolean enable) {
        btns[btn].setEnabled(enable);
        if (!enable) {
            stopTouch();
        }
    }
}
