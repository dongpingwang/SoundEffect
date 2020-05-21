package com.flyaudio.soundeffect.comm.view.attenuator;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class TouchImageView extends ImageView {

    private Paint mPaint;
    private Drawable mTouchCircle;
    private BlurMaskFilter mPointBlurMaskFilter;
    private Position mPosition;
    private float mTextCenter;
    private int mTouchType;

    private static final int EQUAL = 10;

    public TouchImageView(Context context) {
        this(context, null);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPosition = new Position();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setPathEffect(new CornerPathEffect(3));
        mTouchCircle = getResources().getDrawable(R.drawable.touch_img_circle, null);
        mPointBlurMaskFilter = new BlurMaskFilter(17, BlurMaskFilter.Blur.SOLID);
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        mTextCenter = (fontMetricsInt.ascent + fontMetricsInt.descent) / 2f;
        mTouchType = 0;
        setPadding((int) getResources().getDimension(R.dimen.touch_img_scale_space),
                (int) getResources().getDimension(R.dimen.touch_img_scale_space),
                (int) getResources().getDimension(R.dimen.touch_img_edge_space),
                (int) getResources().getDimension(R.dimen.touch_img_edge_space)
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            width = (int) getResources().getDimension(R.dimen.touch_img_view_size);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            height = (int) getResources().getDimension(R.dimen.touch_img_view_size);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制刻度
        drawScale(canvas);
        if (mPosition.getX() < 0 || mPosition.getY() < 0) {
            return;
        }
        // 绘制十字线
        drawCrossLine(canvas);
        // 绘制触摸圆
        drawTouchCircle(canvas);
        // 绘制游标
        drawCursor(canvas);
    }

    private void drawScale(Canvas canvas) {
        mPaint.setStrokeWidth(3.24F);
        mPaint.setColor(getResources().getColor(R.color.text_color_gray, null));
        float hSpace = (getWidth() - getPaddingLeft() - getPaddingRight()) * 1.0F / EQUAL;
        float vSpace = (getHeight() - getPaddingTop() - getPaddingBottom()) * 1.0F / EQUAL;
        float margin = getResources().getDimension(R.dimen.touch_img_space_middle);
        float radius = mPaint.getStrokeWidth();
        for (int i = 1; i < EQUAL; i++) {
            canvas.drawCircle(getPaddingLeft() + i * hSpace, margin, radius, mPaint);
            canvas.drawCircle(margin, getPaddingTop() + i * vSpace, radius, mPaint);
        }
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getResources().getDimension(R.dimen.font_10));
        canvas.drawText("L5", getPaddingLeft(), margin - mTextCenter, mPaint);
        canvas.drawText("R5", getWidth() - getPaddingRight(), margin - mTextCenter, mPaint);
        canvas.drawText("F5", margin, getPaddingTop() - mTextCenter, mPaint);
        canvas.drawText("R5", margin, getHeight() - getPaddingBottom() - mTextCenter, mPaint);
    }

    private void drawCrossLine(Canvas canvas) {
        canvas.save();
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mPaint.setStrokeWidth(4);
        mPaint.setColor(getResources().getColor(R.color.text_color_gray, null));
        canvas.drawLine(getPaddingLeft(), mPosition.y, getWidth(), mPosition.y, mPaint);
        canvas.drawLine(mPosition.x, getPaddingTop(), mPosition.x, getHeight(), mPaint);
        canvas.restore();
    }


    private void drawTouchCircle(Canvas canvas) {
//        canvas.save();
//        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
//        int length = getWidth() / 5;
//        int x = mPosition.x - length / 2;
//        int y = mPosition.y - length / 2;
//        mTouchCircle.setBounds(x, y, x + length, y + length);
//        mTouchCircle.draw(canvas);
//        canvas.restore();
        mPaint.setStrokeWidth(4.0F);
        mPaint.setMaskFilter(mPointBlurMaskFilter);
        mPaint.setColor(getContext().getResources().getColor(R.color.theme_color, null));
        canvas.drawCircle(mPosition.x, mPosition.y, 16, mPaint);
        mPaint.setMaskFilter(null);
    }

    private void drawCursor(Canvas canvas) {
        mPaint.setColor(getResources().getColor(R.color.theme_color, null));
        float margin = getResources().getDimension(R.dimen.touch_img_space_middle);
        float wSize = getResources().getDimension(R.dimen.touch_img_cursor_width) / 2;
        float hSize = getResources().getDimension(R.dimen.touch_img_cursor_height) / 2;
        Path vCursor = new Path();
        vCursor.moveTo(mPosition.x - wSize, margin - hSize);
        vCursor.lineTo(mPosition.x - wSize, margin);
        vCursor.lineTo(mPosition.x, margin + hSize);
        vCursor.lineTo(mPosition.x + wSize, margin);
        vCursor.lineTo(mPosition.x + wSize, margin - hSize);
        vCursor.close();
        canvas.drawPath(vCursor, mPaint);
        Path hCursor = new Path();
        hCursor.moveTo(margin - hSize, mPosition.y - wSize);
        hCursor.lineTo(margin, mPosition.y - wSize);
        hCursor.lineTo(margin + hSize, mPosition.y);
        hCursor.lineTo(margin, mPosition.y + wSize);
        hCursor.lineTo(margin - hSize, mPosition.y + wSize);
        hCursor.close();
        canvas.drawPath(hCursor, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float touchX = event.getX();
        float touchY = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            if (touchX >= getPaddingLeft() && touchY <= getPaddingTop()) {
                mTouchType = 1;
            } else if (touchY >= getPaddingTop() && touchX <= getPaddingLeft()) {
                mTouchType = 2;
            } else {
                mTouchType = 0;
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mTouchType = 0;
            return true;
        }
        int x = mPosition.x;
        int y = mPosition.y;
        if (mTouchType == 0 || mTouchType == 1) {
            x = (int) (touchX + 0.5);
        }
        if (mTouchType == 0 || mTouchType == 2) {
            y = (int) (touchY + 0.5);
        }
        x = x < getPaddingLeft()
                ? getPaddingLeft()
                : x > getWidth() - getPaddingRight()
                ? getWidth() - getPaddingRight()
                : x;
        y = y < getPaddingTop()
                ? getPaddingTop()
                : y > getHeight() - getPaddingBottom()
                ? getHeight() - getPaddingBottom()
                : y;
        mPosition.set(x, y);
        invalidate();
        return true;
    }

    public Position getPosition() {
        return mPosition;
    }

    public void setPosition(int x, int y) {
        mPosition.set(x, y);
        invalidate();
    }

    public void setViewPosition(int x, int y) {
        setPosition(x + getPaddingLeft(), y + getPaddingTop());
    }

    public int getViewWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public int getViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public void setPositionChangedListener(PositionChangedListener positionChangedListener) {
        mPosition.setPositionChangedListener(positionChangedListener);
    }

    public interface PositionChangedListener {
        /**
         * 光标位置发生改变
         *
         * @param x
         * @param y
         */
        void onPositionChanged(int x, int y);
    }

    public static class Position {

        private int x;
        private int y;
        private PositionChangedListener mPositionChangedListener;

        private Position() {
            x = -1;
            y = -1;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        private void set(int x, int y) {
            if (this.x != x && this.y != y) {
                this.x = x;
                this.y = y;
                if (mPositionChangedListener != null) {
                    mPositionChangedListener.onPositionChanged(getX(), getY());
                }
            }
        }

        private void setPositionChangedListener(PositionChangedListener positionChangedListener) {
            mPositionChangedListener = positionChangedListener;
        }
    }
}
