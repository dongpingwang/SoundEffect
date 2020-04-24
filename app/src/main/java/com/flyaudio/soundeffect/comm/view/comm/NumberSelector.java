package com.flyaudio.soundeffect.comm.view.comm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
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

    private ValueFormater valueFormater;
    private OnNumberChangedListener onNumberChangedListener;
    private long delayMillis;
    private Runnable touchRunable;
    private IInputValueListener mInputValueListener;

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
        findView();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberSelector);
        titleTv.setText(typedArray.getString(R.styleable.NumberSelector_text));
        int titleVisibility = typedArray.getInt(R.styleable.NumberSelector_visibility_title, -1);
        if (titleVisibility == -1)
            titleVisibility = titleTv.getText().length() == 0 ? GONE : VISIBLE;
        titleTv.setVisibility(titleVisibility);
        max = typedArray.getInteger(R.styleable.NumberSelector_max, 10);
        min = typedArray.getInteger(R.styleable.NumberSelector_min, 0);
        isLoop = typedArray.getBoolean(R.styleable.NumberSelector_is_loop, false);
        step = typedArray.getInteger(R.styleable.NumberSelector_step, 1);
        setValue(typedArray.getInteger(R.styleable.NumberSelector_value, min));
        setEnabled(typedArray.getBoolean(R.styleable.NumberSelector_selector_enable, true));
        typedArray.recycle();
        touchRunable = new Runnable() {
            @Override
            public void run() {
                View touchView = (View) valueTv.getTag();
                if (touchView == null)
                    return;
                int number = getValue();
                if (touchView == leftBtn)
                    number = (number == min && isLoop) ? max : number - step;
                else if (touchView == rightBtn)
                    number = (number == max && isLoop) ? min : number + step;
                setValue(number, true);
                postDelayed(touchRunable, delayMillis);
                if (delayMillis != PRESS_DELAY_MILLIS)
                    delayMillis = PRESS_DELAY_MILLIS;
            }
        };
        leftBtn.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return NumberSelector.this.onTouch(view, motionEvent);
            }
        });
        rightBtn.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return NumberSelector.this.onTouch(view, motionEvent);
            }
        });
    }

    private void findView() {
        titleTv = getView(R.id.title);
        leftBtn = getView(R.id.left_btn);
        valueTv = getView(R.id.value);
        rightBtn = getView(R.id.right_btn);

        valueTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mInputValueListener != null){
                    mInputValueListener.value(getValue());
                }
            }
        });
    }

    private <T extends View> T getView(@IdRes int id) {
        return (T) findViewById(id);
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
        int sizeLenght = (int) (Math.sqrt(w * w + h * h) + 0.5f);

        if (action == MotionEvent.ACTION_DOWN) {
            delayMillis = CLICK_DELAY_MILLIS;
            valueTv.setTag(view);
            removeCallbacks(touchRunable);
            post(touchRunable);
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_OUTSIDE
                || action == MotionEvent.ACTION_CANCEL
                || x < (w - sizeLenght) / 2 || x > w + (sizeLenght - w) / 2
                || y < (h - sizeLenght) / 2 || y > h + (sizeLenght - h) / 2) {
            delayMillis = CLICK_DELAY_MILLIS;
            removeCallbacks(touchRunable);
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
            if (onNumberChangedListener != null)
                onNumberChangedListener.onNumberChanged(oldValue, this.value, byTouch);
        }
        if (valueFormater == null)
            valueFormater = new ValueFormater() {
                @Override
                public void valueFormat(@NonNull TextView t, int v) {
                    t.setText(String.valueOf(v));
                }
            };
        valueFormater.valueFormat(valueTv, this.value);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (this.max == max)
            return;
        this.max = max;
        if (this.max < getValue())
            setValue(this.max);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        if (this.min == min)
            return;
        this.min = min;
        if (this.min > getValue())
            setValue(this.min);
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

    public void setValueFormater(ValueFormater valueFormater) {
        if (this.valueFormater == valueFormater)
            return;
        this.valueFormater = valueFormater;
        setValue(getValue());
    }

    public interface OnNumberChangedListener {
        void onNumberChanged(int oldNum, int newNum, boolean byTouch);
    }

    public interface ValueFormater {
        void valueFormat(@NonNull TextView textView, int value);
    }

    public void setInputValueListener(IInputValueListener listener){
        mInputValueListener = listener;
    }
    public interface  IInputValueListener{
        void value(int oldValue);
    }
}
