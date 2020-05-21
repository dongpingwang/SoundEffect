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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flyaudio.lib.utils.ConvertUtils;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

/**
 * created by Liu Zicong 19-06-05
 */
public class SoundFieldCoordinateView extends View {

    private final static int EQUAL = 10 + 2;    //坐标分多少份
    private Paint mViewPaint;
    private Paint mTextPaint;
    private float mTouchPointX;
    private float mTouchPointY;
    private int mViewWidth = -1;
    private int mViewHeight = -1;
    private Bitmap mCoordinateArrow;
    private Bitmap mCoordinateArrow90;
    private BlurMaskFilter mPointBlurMaskFilter;
    private IAttenuatorListener mAttenuatorListener;

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
        mTextPaint.setTextSize(ConvertUtils.sp2px(9));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(ResUtils.getColor(R.color.text_color_gray));

        mCoordinateArrow = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coordinate_arrow).copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        mCoordinateArrow90 = Bitmap.createBitmap(mCoordinateArrow, 0, 0, mCoordinateArrow.getWidth(), mCoordinateArrow.getHeight(), matrix, false);
        mPointBlurMaskFilter = new BlurMaskFilter(16, BlurMaskFilter.Blur.SOLID);
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (mViewWidth == -1 || mViewHeight == -1) {
            mViewWidth = width;
            mViewHeight = height;
            checkView();
        }
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("L5", width / EQUAL, 10, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("R5", width / EQUAL * 11, 10, mTextPaint);
        canvas.drawText("F5", 1, height / EQUAL, mTextPaint);
        canvas.drawText("R5", 1, height / EQUAL * 11 + 5, mTextPaint);


        mViewPaint.setStrokeWidth(6);
        mViewPaint.setColor(ResUtils.getColor(R.color.text_color_gray));
        for (int i = 2; i < EQUAL - 1; i++) {
            canvas.drawPoint(width / EQUAL * i, 6, mViewPaint);
            canvas.drawPoint(6, height / EQUAL * i, mViewPaint);

        }

        mViewPaint.setStrokeWidth(4);
        canvas.drawLine(mTouchPointX - 1, 34, mTouchPointX - 1, height - 36, mViewPaint);

        canvas.drawLine(34, mTouchPointY, width - 36, mTouchPointY, mViewPaint);
        mViewPaint.setColorFilter(new PorterDuffColorFilter(ResUtils.getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mCoordinateArrow, mTouchPointX - 8.5f, 1, mViewPaint);
        canvas.drawBitmap(mCoordinateArrow90, 0, mTouchPointY - 8, mViewPaint);
        mViewPaint.setColorFilter(null);
        mViewPaint.setMaskFilter(mPointBlurMaskFilter);
        mViewPaint.setColor(ResUtils.getColor(R.color.theme_color));
        canvas.drawCircle(mTouchPointX - 1, mTouchPointY, 15, mViewPaint);
        mViewPaint.setMaskFilter(null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateTouchPoint(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                updateTouchUpPointXY(x, y);
                break;
            default:
                break;
        }
        return true;
    }

    private void updateTouchPoint(float x, float y) {
        x = limitValue(x, y)[0];
        y = limitValue(x, y)[1];
        mTouchPointX = x;
        mTouchPointY = y;
        invalidate();
    }

    private void updateTouchUpPointXY(float x, float y) {
        x = limitValue(x, y)[0];
        y = limitValue(x, y)[1];
        float remainderX = x % (mViewWidth / EQUAL);
        int divisorX = (int) x / (mViewWidth / EQUAL);
        if (remainderX > (mViewWidth / EQUAL / 2))
            mTouchPointX = (mViewWidth / EQUAL) * (divisorX + 1);
        else
            mTouchPointX = (mViewWidth / EQUAL) * divisorX;
        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(x, mTouchPointX);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        valueAnimatorX.setDuration(300);
        valueAnimatorX.start();


        float remainderY = y % (mViewHeight / EQUAL);
        int divisorY = (int) y / (mViewHeight / EQUAL);
        if (remainderY > (mViewHeight / EQUAL / 2))
            mTouchPointY = (mViewHeight / EQUAL) * (divisorY + 1);
        else
            mTouchPointY = (mViewHeight / EQUAL) * divisorY;
        ValueAnimator valueAnimatorY = ValueAnimator.ofFloat(y, mTouchPointY);
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        valueAnimatorY.setDuration(300);
        valueAnimatorY.start();
        if (mAttenuatorListener != null)
            mAttenuatorListener.setBalance();
    }


    public interface IAttenuatorListener {
        void setBalance();

        void isToEdge(int btn, boolean edge);
    }

    public void setAttenutorListener(IAttenuatorListener listener) {
        mAttenuatorListener = listener;
    }

    public void changeXLeftRight(boolean isRight) {
        checkView();
        int divisorX = (int) mTouchPointX / (mViewWidth / EQUAL);
        divisorX = isRight ? divisorX + 1 : divisorX - 1;
        divisorX = divisorX > 11 ? 11 : divisorX < 1 ? 1 : divisorX;
        if (mAttenuatorListener != null) {
            mAttenuatorListener.isToEdge(isRight ? AttenuatorAdjuster.AttenuatorBtn.BTN_RIGHT : AttenuatorAdjuster.AttenuatorBtn.BTN_LEFT, isRight ? divisorX < 11 : divisorX > 1);
            mAttenuatorListener.isToEdge(isRight ? AttenuatorAdjuster.AttenuatorBtn.BTN_LEFT : AttenuatorAdjuster.AttenuatorBtn.BTN_RIGHT, true);
        }
        mTouchPointX = (mViewWidth / EQUAL) * divisorX;
        invalidate();
    }

    public void changeYUpDown(boolean isDown) {
        checkView();
        int divisorY = (int) mTouchPointY / (mViewHeight / EQUAL);
        divisorY = isDown ? divisorY + 1 : divisorY - 1;
        divisorY = divisorY > 11 ? 11 : divisorY < 1 ? 1 : divisorY;
        if (mAttenuatorListener != null) {
            mAttenuatorListener.isToEdge(isDown ? AttenuatorAdjuster.AttenuatorBtn.BTN_DOWN : AttenuatorAdjuster.AttenuatorBtn.BTN_UP, isDown ? divisorY < 11 : divisorY > 1);
            mAttenuatorListener.isToEdge(isDown ? AttenuatorAdjuster.AttenuatorBtn.BTN_UP : AttenuatorAdjuster.AttenuatorBtn.BTN_DOWN, true);
        }
        mTouchPointY = (mViewHeight / EQUAL) * divisorY;

        invalidate();
    }

    public void goToCenter() {
        checkView();
        mTouchPointX = (mViewWidth / EQUAL) * 6;
        mTouchPointY = (mViewHeight / EQUAL) * 6;
        invalidate();
        if (mAttenuatorListener != null) {
            for (int i = AttenuatorAdjuster.AttenuatorBtn.BTN_UP; i <= AttenuatorAdjuster.AttenuatorBtn.BTN_RIGHT; i++) {
                mAttenuatorListener.isToEdge(i, true);
            }
        }
    }

    public int[] getPos() {
        checkView();
        int x = (int) (mTouchPointX / (mViewHeight / EQUAL));
        int y = (int) (mTouchPointY / (mViewHeight / EQUAL));
        return new int[]{x, y};
    }

    public void setPos(@NonNull int[] pos) {
        checkView();
        mTouchPointX = (mViewWidth / EQUAL) * pos[0];
        mTouchPointY = (mViewHeight / EQUAL) * pos[1];
        invalidate();
    }

    private void checkView() {
        if (mViewWidth == -1 || mViewHeight == -1) {
            mViewWidth = 396;
            mViewHeight = 396;
        }
    }

    private float[] limitValue(float x, float y) {
        x = x > mViewWidth / EQUAL * 11 ? mViewWidth / EQUAL * 11 : x < mViewWidth / EQUAL ? mViewWidth / EQUAL : x;
        y = y > mViewHeight / EQUAL * 11 ? mViewHeight / EQUAL * 11 : y < mViewHeight / EQUAL ? mViewHeight / EQUAL : y;
        return new float[]{x, y};
    }

}
