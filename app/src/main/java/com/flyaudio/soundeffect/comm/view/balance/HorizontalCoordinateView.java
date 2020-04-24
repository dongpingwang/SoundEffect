package com.flyaudio.soundeffect.comm.view.balance;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flyaudio.lib.utils.ConvertUtils;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * @date 2019.12.24
 */
public class HorizontalCoordinateView extends View {

    private final static int EQUAL = 10 + 2;    //坐标分多少份
    private Paint mViewPaint;
    private Paint mTextPaint;
    private float mTouchPointX, mTouchPointY;
    private int mViewWidth = -1;
    private int mViewHeight = -1;
    private Bitmap mCoordinateArrow;
    private BlurMaskFilter mPointBlurMaskFilter;
    private IAttenutorListener mAttenutorListener;

    public HorizontalCoordinateView(Context context) {
        this(context, null, 0);
    }

    public HorizontalCoordinateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(ConvertUtils.sp2px(9));
        mTextPaint.setColor(ResUtils.getColor(R.color.text_color_gray));
        mCoordinateArrow = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coordinate_arrow).copy(Bitmap.Config.ARGB_8888, true);
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
            if (mViewWidth == -1 || mViewHeight == -1) {
                mViewWidth = 396;
                mViewHeight = 396;
            }
        }
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("L5", width / EQUAL, 10, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("R5", width / EQUAL * 11, 10, mTextPaint);

        mViewPaint.setStrokeWidth(6);
        mViewPaint.setColor(ResUtils.getColor(R.color.text_color_gray));
        for (int i = 2; i < EQUAL - 1; i++) {
            canvas.drawPoint(width / EQUAL * i, 6, mViewPaint);
        }
        mViewPaint.setStrokeWidth(4);
        canvas.drawLine(34, mTouchPointY, width - 36, mTouchPointY, mViewPaint);
        mViewPaint.setColorFilter(new PorterDuffColorFilter(ResUtils.getColor(R.color.theme_color), PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mCoordinateArrow, mTouchPointX - 8.5f, 1, mViewPaint);

        mViewPaint.setColorFilter(null);
        mViewPaint.setMaskFilter(mPointBlurMaskFilter);
        mViewPaint.setColor(ResUtils.getColor(R.color.theme_color));
        canvas.drawCircle(mTouchPointX - 1, mTouchPointY, 15, mViewPaint);
        mViewPaint.setMaskFilter(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                updateTouchUpPointXY(x);
                break;
            default:
                break;
        }
        return true;
    }

    private void updateTouchUpPointXY(float x) {
        x = x > mViewWidth / EQUAL * 11 ? mViewWidth / EQUAL * 11 : x < mViewWidth / EQUAL ? mViewWidth / EQUAL : x;

        float remainderX = x % (mViewWidth / EQUAL);
        int divisorX = (int) x / (mViewWidth / EQUAL);
        mTouchPointX = remainderX > (mViewWidth / EQUAL / 2) ? mTouchPointX = (mViewWidth / EQUAL) * (divisorX + 1) : (mViewWidth / EQUAL) * divisorX;
        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(x, mTouchPointX);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        valueAnimatorX.setDuration(300);
        valueAnimatorX.start();

        if (mAttenutorListener != null) {
            mAttenutorListener.setBalance();
        }
    }


    public interface IAttenutorListener {
        void setBalance();
        void isToEdge(int btn, boolean edge);
    }

    public void setAttenutorListener(IAttenutorListener listener) {
        mAttenutorListener = listener;
    }

    public int getPosX() {
        checkView();
        int x = (int) (mTouchPointX / (mViewHeight / EQUAL));
        return x;
    }

    public void setPos(int x) {
        checkView();
        mTouchPointX = (mViewWidth / EQUAL) * x;
        invalidate();
    }

    public void goToCenter() {
        checkView();
        mTouchPointX = (mViewWidth / EQUAL) * 6;
        mTouchPointY = mViewHeight / 3.4F;
        invalidate();
        if (mAttenutorListener != null) {
            for (int i = AttenuatorAdjuster.BTN_UP; i <= AttenuatorAdjuster.BTN_RIGHT; i++) {
                mAttenutorListener.isToEdge(i,true);
            }
        }
    }

    public void changeXLeftRight(boolean isRight) {
        checkView();
        int divisorX = (int) mTouchPointX / (mViewWidth / EQUAL);
        divisorX = isRight ? divisorX + 1 : divisorX - 1;
        divisorX = divisorX > 11 ? 11 : divisorX < 1 ? 1 : divisorX;
        mTouchPointX = (mViewWidth / EQUAL) * divisorX;
        if (mAttenutorListener != null) {
            mAttenutorListener.isToEdge(isRight ? AttenuatorAdjuster.BTN_RIGHT : AttenuatorAdjuster.BTN_LEFT,isRight ? divisorX < 11 : divisorX > 1);
            mAttenutorListener.isToEdge(isRight ? AttenuatorAdjuster.BTN_LEFT : AttenuatorAdjuster.BTN_RIGHT,true);
        }
        invalidate();
    }

    private void checkView() {
        if (mViewWidth == -1 || mViewHeight == -1) {
            mViewWidth = 396;
            mViewHeight = 396;
        }
    }

}
