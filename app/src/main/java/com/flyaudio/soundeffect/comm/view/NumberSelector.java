package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class NumberSelector extends FrameLayout {

    private static final long CLICK_DELAY_MILLIS = 800;
    private static final long PRESS_DELAY_MILLIS = 50;

    private int max;
    private int min;
    private boolean isLoop;
    private int step;
    private int value;
    private TextView titleTv;
    private ImageButton leftBtn;
    private TextView valueTv;
    private ImageButton rightBtn;

    private ValueFormatter valueFormatter;
    private OnNumberChangedListener onNumberChangedListener;
    private long delayMillis;
    private Runnable touchRunnable;


    public NumberSelector(@NonNull Context context) {
        this(context, null);
    }

    public NumberSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.view_number_selector, this);
        titleTv = (TextView) findViewById(R.id.title);
        leftBtn = (ImageButton) findViewById(R.id.left_btn);
        valueTv = (TextView) findViewById(R.id.value);
        rightBtn = (ImageButton) findViewById(R.id.right_btn);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberSelector);
        titleTv.setText(typedArray.getString(R.styleable.NumberSelector_text));
        int titleVisibility = typedArray.getInt(R.styleable.NumberSelector_visibility_title, -1);
        if (titleVisibility == -1) {
            titleVisibility = titleTv.getText().length() == 0 ? GONE : VISIBLE;
        }

        titleTv.setVisibility(titleVisibility);
        max = typedArray.getInteger(R.styleable.NumberSelector_max, 10);
        min = typedArray.getInteger(R.styleable.NumberSelector_min, 0);
        isLoop = typedArray.getBoolean(R.styleable.NumberSelector_is_loop, false);
        step = typedArray.getInteger(R.styleable.NumberSelector_step, 1);
        setValue(typedArray.getInteger(R.styleable.NumberSelector_value, min));
        setEnabled(typedArray.getBoolean(R.styleable.NumberSelector_selector_enable, true));
        typedArray.recycle();
        touchRunnable = new Runnable() {
            @Override
            public void run() {
                View touchView = (View) valueTv.getTag();
                if (touchView != null) {
                    int number = getValue();
                    if (touchView == leftBtn) {
                        number = (number == min && isLoop) ? max : number - step;
                    } else if (touchView == rightBtn) {
                        number = (number == max && isLoop) ? min : number + step;
                    }
                    setValue(number, true);
                    postDelayed(touchRunnable, delayMillis);
                    if (delayMillis != PRESS_DELAY_MILLIS) {
                        delayMillis = PRESS_DELAY_MILLIS;
                    }
                }
            }
        };
        leftBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return NumberSelector.this.onTouch(view, motionEvent);
            }

        });
        rightBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return NumberSelector.this.onTouch(view, motionEvent);
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        titleTv.setEnabled(enabled);
        leftBtn.setEnabled(enabled);
        valueTv.setEnabled(enabled);
        rightBtn.setEnabled(enabled);
        valueTv.setVisibility(enabled ? VISIBLE : INVISIBLE);
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
            valueTv.setTag(view);
            removeCallbacks(touchRunnable);
            post(touchRunnable);
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_OUTSIDE
                || action == MotionEvent.ACTION_CANCEL
                || x < (w - sizeLength) / 2 || x > w + (sizeLength - w) / 2
                || y < (h - sizeLength) / 2 || y > h + (sizeLength - h) / 2) {
            delayMillis = CLICK_DELAY_MILLIS;
            removeCallbacks(touchRunnable);
            valueTv.setTag(null);
        }
        return false;
    }

    public int getValue() {
        return value < min ? min : value > max ? max : value;
    }

    public void setValue(int value) {
        setValue(value, false);
    }

    private void setValue(int value, boolean byTouch) {
        if (this.value != value) {
            int oldValue = this.value;
            this.value = value < min ? min : value > max ? max : value;
            if (onNumberChangedListener != null) {
                onNumberChangedListener.onNumberChanged(oldValue, this.value, byTouch);
            }
        }
        if (valueFormatter == null) {
            valueFormatter = new ValueFormatter() {
                @Override
                public void valueFormat(@NonNull TextView t, int v) {
                    t.setText(String.valueOf(v));
                }
            };
        }
        valueFormatter.valueFormat(valueTv, this.value);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (this.max != max) {
            this.max = max;
            if (this.max < getValue()) {
                setValue(this.max);
            }
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        if (this.min != min) {
            this.min = min;
            if (this.min > getValue()) {
                setValue(this.min);
            }
        }
    }


    public void setAdjustRange(int min, int max) {
        setMin(min);
        setMax(max);
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setAdjusting(boolean adjusting) {
        valueTv.setTextColor(adjusting ? ResUtils.getColor(R.color.theme_color) : ResUtils.getColor(R.color.number_selector_text_color));
    }

    public void setOnNumberChangedListener(OnNumberChangedListener numberChangedListener) {
        this.onNumberChangedListener = numberChangedListener;
    }

    public void setValueFormatter(ValueFormatter valueFormatter) {
        if (this.valueFormatter != valueFormatter) {
            this.valueFormatter = valueFormatter;
            setValue(getValue());
        }
    }

    public interface OnNumberChangedListener {
        /**
         * 调节的值变化
         *
         * @param oldNum  上一个值
         * @param newNum  现在的值
         * @param byTouch 是否点击了， true表示是
         */
        void onNumberChanged(int oldNum, int newNum, boolean byTouch);
    }

    public interface ValueFormatter {
        /**
         * 数字调节器的文本显示样式
         *
         * @param textView 显示值的TextView
         * @param value    设置格式后的值
         */
        void valueFormat(@NonNull TextView textView, int value);
    }
}
