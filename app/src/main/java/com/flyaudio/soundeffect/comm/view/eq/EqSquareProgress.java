package com.flyaudio.soundeffect.comm.view.eq;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.util.ColorUtils;


/**
 * @author Dongping Wang
 * @date 2020/3/4 22:54
 * email wangdongping@flyaudio.cn
 */
public class EqSquareProgress extends View {

    private Paint paint;
    private int max, min, step;
    private int colorDefault, colorAdjusting, colorAdjustedStart, colorAdjustedEnd;
    private int progress;
    private boolean adjusting;

    public EqSquareProgress(Context context) {
        this(context, null);
    }

    public EqSquareProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqSquareProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(context.getResources().getDimension(R.dimen.eq_progress_line));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EqSquareProgress);
        max = typedArray.getInteger(R.styleable.EqSquareProgress_max, 14);
        min = typedArray.getInteger(R.styleable.EqSquareProgress_min, -14);
        step = typedArray.getInteger(R.styleable.EqSquareProgress_step, 1);
        colorDefault = typedArray.getColor(R.styleable.EqSquareProgress_default_color, ResUtils.getColor(R.color.eq_progress_default_color));
        colorAdjusting = typedArray.getColor(R.styleable.EqSquareProgress_adjusting_color, ResUtils.getColor(R.color.eq_progress_adjusting_color));
        colorAdjustedStart = typedArray.getColor(R.styleable.EqSquareProgress_adjusted_color, ResUtils.getColor(R.color.eq_progress_gradient_end_color));
        colorAdjustedEnd = typedArray.getColor(R.styleable.EqSquareProgress_adjusted_color, ResUtils.getColor(R.color.eq_progress_gradient_start_color));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = (max - min) / step + 1;
        for (int i = 0; i < count; i++) {
            // 从下往上绘制
            float startX = getPaddingLeft();
            float startY = getHeight() - ResUtils.getDimension(R.dimen.eq_progress_line_all) * i - ResUtils.getDimension(R.dimen.eq_progress_line);
            float stopX = startX + getWidth() + getPaddingRight();
            float stopY = startY;
            if (min + i * step <= progress) {
                int currentColor = ColorUtils.getGradientColor(i * 1.0F / (count - 1), colorAdjustedStart, colorAdjustedEnd);
                paint.setColor(currentColor);
            } else {
                paint.setColor(colorDefault);
            }
            if (adjusting && min + i * step == progress) {
                paint.setColor(colorAdjusting);
            }
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.progress = this.progress > max ? max : this.progress < min ? min : this.progress;
        invalidate();
    }

    public void setAdjusting(boolean adjusting) {
        this.adjusting = adjusting;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setMin(int min) {
        this.min = min;
        invalidate();
    }

    public void setStep(int step) {
        this.step = step;
        invalidate();
    }


}
