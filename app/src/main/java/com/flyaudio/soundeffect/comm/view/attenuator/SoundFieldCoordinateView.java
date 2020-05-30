package com.flyaudio.soundeffect.comm.view.attenuator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;


/**
 * @author Liu Zicong
 * date 19-06-05
 */
public class SoundFieldCoordinateView extends View implements ValueAnimator.AnimatorUpdateListener {

    private final static int EQUAL = 12;
    private final static int MAX_POS = 11;
    private final static int MIN_POS = 1;

    private Paint mViewPaint;
    private Paint mTextPaint;
    private Position position;
    private float xPos, yPos;
    private Bitmap mCoordinateArrow;
    private Bitmap mCoordinateArrow90;
    private BlurMaskFilter mPointBlurMaskFilter;
    private PorterDuffColorFilter mPorterDuffColorFilter;
    private static float wSpace, hSpace;
    private ValueAnimator xValueAnimator = new ValueAnimator();
    private ValueAnimator yValueAnimator = new ValueAnimator();

    private static boolean showYCoordinate = true;
    private static boolean byTouch = false;

    public SoundFieldCoordinateView(Context context) {
        this(context, null, 0);
    }

    public SoundFieldCoordinateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundFieldCoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mViewPaint = new Paint();
        mViewPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mViewPaint.setStrokeCap(Paint.Cap.ROUND);
        mViewPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.sound_field_coordinate_view_text));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(ResUtils.getColor(R.color.text_color_gray));

        mCoordinateArrow = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coordinate_arrow).copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        mCoordinateArrow90 = Bitmap.createBitmap(mCoordinateArrow, 0, 0, mCoordinateArrow.getWidth(), mCoordinateArrow.getHeight(), matrix, false);
        mPointBlurMaskFilter = new BlurMaskFilter(16, BlurMaskFilter.Blur.SOLID);
        mPorterDuffColorFilter = new PorterDuffColorFilter(ResUtils.getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN);
        setWillNotDraw(false);

        position = new Position();
        xValueAnimator.addUpdateListener(this);
        yValueAnimator.addUpdateListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wSpace = getWidth() * 1.0F / EQUAL;
        hSpace = getHeight() * 1.0F / EQUAL;
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("L5", wSpace * MIN_POS, 10, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("R5", wSpace * MAX_POS, 10, mTextPaint);
        if (showYCoordinate) {
            canvas.drawText("F5", 1, hSpace * MIN_POS, mTextPaint);
            canvas.drawText("R5", 1, hSpace * MAX_POS + 5, mTextPaint);
        }
        mViewPaint.setStrokeWidth(6);
        mViewPaint.setColor(ResUtils.getColor(R.color.text_color_gray));
        for (int i = 2; i < EQUAL - 1; i++) {
            canvas.drawPoint(wSpace * i, 6, mViewPaint);
            if (showYCoordinate) {
                canvas.drawPoint(6, hSpace * i, mViewPaint);
            }
        }
        mViewPaint.setStrokeWidth(4);
        canvas.drawLine(34, yPos, getWidth() - 36, yPos, mViewPaint);
        if (showYCoordinate) {
            canvas.drawLine(xPos - 1, 34, xPos - 1, getHeight() - 36, mViewPaint);
        }

        mViewPaint.setColorFilter(mPorterDuffColorFilter);
        canvas.drawBitmap(mCoordinateArrow, xPos - 8.5f, 1, mViewPaint);
        if (showYCoordinate) {
            canvas.drawBitmap(mCoordinateArrow90, 0, yPos - 8, mViewPaint);
        }
        mViewPaint.setColorFilter(null);
        mViewPaint.setMaskFilter(mPointBlurMaskFilter);
        mViewPaint.setColor(ResUtils.getColor(R.color.theme_color));
        canvas.drawCircle(xPos - 1, yPos, 15, mViewPaint);
        mViewPaint.setMaskFilter(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                xPos = x;
                yPos = y;
                if (!showYCoordinate) {
                    yPos = getHeight() * 1.0F / 2;
                }
                xPos = xPos > wSpace * MAX_POS ? wSpace * MAX_POS : xPos < wSpace ? wSpace : xPos;
                yPos = yPos > hSpace * MAX_POS ? hSpace * MAX_POS : yPos < hSpace ? hSpace : yPos;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                updateTouchPos(x, y);
                break;
            default:
                break;
        }
        return true;
    }


    private void updateTouchPos(float x, float y) {
        byTouch = true;
        float xDiff = x % wSpace;
        int xCoordinate = (int) (x / wSpace);
        if (xDiff * 2 > wSpace) {
            xCoordinate = xCoordinate + 1;
        }

        float yDiff = y % hSpace;
        int yCoordinate = (int) (y / hSpace);
        if (yDiff * 2 > hSpace) {
            yCoordinate = yCoordinate + 1;
        }
        setPosition(xCoordinate, yCoordinate);
        xValueAnimator.setFloatValues(x, xPos);
        xValueAnimator.setDuration(300);
        xValueAnimator.start();
        yValueAnimator.setFloatValues(y, yPos);
        yValueAnimator.setDuration(300);
        yValueAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (xValueAnimator.isRunning()) {
            xValueAnimator.cancel();
        }
        if (yValueAnimator.isRunning()) {
            yValueAnimator.cancel();
        }
        byTouch = false;
    }

    public void changePosX(boolean isRight) {
        int x = position.getX();
        x = isRight ? x + 1 : x - 1;
        byTouch = true;
        setPosition(x, position.getY());
    }

    public void changePosY(boolean isDown) {
        int y = position.getY();
        y = isDown ? y + 1 : y - 1;
        byTouch = true;
        setPosition(position.getX(), y);
    }

    public void goToCenter() {
        byTouch = true;
        setPosition(EQUAL / 2, EQUAL / 2);
    }

    public void setPosition(int x, int y) {
        position.set(x, y);
        xPos = position.getX() * wSpace;
        yPos = position.getY() * hSpace;
        if (!showYCoordinate) {
            yPos = getHeight() * 1.0F / 2;
        }
        invalidate();
    }

    public void setShowYCoordinate(boolean showYCoordinate) {
        SoundFieldCoordinateView.showYCoordinate = showYCoordinate;
        invalidate();
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
            if (this.x != x || this.y != y) {
                this.x = x;
                this.y = y;
                this.x = this.x > MAX_POS ? MAX_POS : this.x < MIN_POS ? MIN_POS : this.x;
                this.y = this.y > MAX_POS ? MAX_POS : this.y < MIN_POS ? MIN_POS : this.y;
                if (mPositionChangedListener != null) {
                    mPositionChangedListener.onPositionChanged(this.x, this.y, byTouch);
                }
            }
        }

        private void setPositionChangedListener(PositionChangedListener positionChangedListener) {
            mPositionChangedListener = positionChangedListener;
        }
    }

    public interface PositionChangedListener {
        /**
         * 坐标发生改变
         *
         * @param x       x坐标，范围为0 - 11
         * @param y       y坐标，范围为0 - 11
         * @param byTouch 是否触摸调节
         */
        void onPositionChanged(int x, int y, boolean byTouch);
    }

    public void setPositionChangedListener(PositionChangedListener positionChangedListener) {
        position.setPositionChangedListener(positionChangedListener);
    }
}
