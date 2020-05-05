package com.flyaudio.soundeffect.comm.view.filter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class ViewFrequencyAdjust extends View {

    // 直线画笔
    private Paint paintLine;
    // 文字画笔
    private Paint paintText;
    // 圆点画笔
    private Paint paintDot;
    // 可调节线画笔
    private Paint paintTouchLine;
    // 顶部Y轴单位高度
    private int mTopUnitHeight;
    //X轴底部字体的高度
    private int mBottomTextHeight;
    //Y轴左边的文字宽度
    private int mLeftTextWidth;
    //x y 轴的长度
    private int yHeight, xWidth;
    //x y 轴坐标值
    private String[] xLineStr = new String[]{"-18dB", "0dB"};
    private String[] yLineStr = new String[]{"", "20", "25", "32", "40", "50", "63", "80", "100", "125", "160", "200", "Hz"};

    private static List<TouchLine> mTouchLineList;
    private static final double MIN_VALUE = 50;
    private static final double MAX_VALUE = 200;
    private static final double MIN_ANGLE = 10;
    private static final double MAX_ANGLE = 90 - MIN_ANGLE;
    private static final long GUIDE_VIEW_SHOW_TIME = 600;
    private ViewFrequencyAdjust.TouchLine mCurrentTouchLine;
    // -1 代表没触碰,0 代表非端点,1 代表端点
    private int mTouchType;
    // 斜线的长度
    private float mAdjustLineLength;

    private float mTouchRange;
    private float mPreTouchX;
    private float mPreTouchY;

    private int mViewGuideAlpha;
    private ValueAnimator mViewGuideAphaAnimator;
    private ViewFrequencyAdjust.OnTouchLineListener mOnTouchLineListener;

    /**
     * 是否能够触摸进行调节
     */
    private boolean isTouchOn() {
        return false;
    }


    public ViewFrequencyAdjust(Context context) {
        this(context, null);
    }

    public ViewFrequencyAdjust(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewFrequencyAdjust(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mAdjustLineLength = -1;
        mCurrentTouchLine = null;
        mTouchType = -1;
        mTouchLineList = new LinkedList<>();
        mTouchRange = ResUtils.getDimension(R.dimen.filter_space_large);
        mViewGuideAlpha = 0xFF;

        mBottomTextHeight = dp2px(30);
        mLeftTextWidth = dp2px(40);
        mTopUnitHeight = dp2px(20);

        mViewGuideAphaAnimator = ValueAnimator.ofInt(0xFF, 0x00);
        mViewGuideAphaAnimator.setDuration(GUIDE_VIEW_SHOW_TIME);
        mViewGuideAphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mViewGuideAphaAnimator.setRepeatCount(Animation.INFINITE);
        mViewGuideAphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mViewGuideAlpha = (int) valueAnimator.getAnimatedValue();
                updateView();
            }
        });
        mViewGuideAphaAnimator.start();


        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paintLine.setStrokeWidth(dp2px(3.5F));
        paintLine.setColor(getResources().getColor(R.color.view_frequency_adjust_line, null));

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paintText.setColor(getResources().getColor(R.color.view_frequency_adjust_text, null));
        paintText.setTextSize(sp2px(12));

        paintDot = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paintDot.setStrokeWidth(dp2px(3.5F));
        paintDot.setStrokeCap(Paint.Cap.ROUND);
        paintDot.setColor(getResources().getColor(R.color.view_frequency_adjust_line_dot, null));

        paintTouchLine = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAdjustLineLength < 0) {
            mAdjustLineLength = getHeight() * 1.0F / 2;
        }
        // 绘制背景
        drawBg(canvas);
        // 绘制xy
        drawLineXY(canvas);
        // 绘制调节线
        drawViewTouch(canvas);
        if (isTouchOn()) {
            // 调节引导
            drawViewGuide(canvas);
        }

    }

    public void updateView() {
        invalidate();
    }

    private void drawBg(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.view_frequency_adjust_bg, null));
    }

    private void drawLineXY(Canvas canvas) {

        Point start = new Point();
        Point stop = new Point();

        int xLineCount = xLineStr.length;

        xWidth = getWidth() - getPaddingLeft() - getPaddingRight() - mLeftTextWidth;
        yHeight = getHeight() - getPaddingTop() - getPaddingBottom() - mBottomTextHeight - mTopUnitHeight;
        int xLineMarginHeight = yHeight / (xLineCount - 1);

        for (int i = 0; i < xLineCount; i++) {
            start.x = getPaddingLeft() + mLeftTextWidth;
            start.y = getHeight() - getPaddingBottom() - mBottomTextHeight - xLineMarginHeight * i;
            stop.x = xWidth;
            stop.y = start.y;
            if (i == 0  /*||i == xLineCount - 1*/) {
                //绘制横轴
                canvas.drawLine(start.x, start.y, stop.x, stop.y, paintLine);
            }
            // 绘制纵坐标单位
            String drawText = xLineStr[i];
            Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
            float offsetY = ((fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 + fontMetrics.bottom) * 1.0F / 2;
            float baseLine = start.y + offsetY;
            if (i == 0) {
                baseLine = (float) ((mAdjustLineLength * Math.sin(Math.toRadians(75)) + 0.5f) + getPaddingTop() + mTopUnitHeight + offsetY);
            }
            float left = getPaddingLeft() + mLeftTextWidth * 1.0F / 2 - paintText.measureText(drawText) / 2;
            canvas.drawText(drawText, left, baseLine, paintText);
        }

        // 绘制竖线
        int yLinesCount = yLineStr.length;
        int distance = xWidth / yLinesCount;
        for (int i = 0; i < yLinesCount; i++) {
            start.x = distance * i + getPaddingLeft() + mLeftTextWidth;
            start.y = getHeight() - getPaddingBottom() - mBottomTextHeight;

            stop.x = start.x;
            stop.y = getPaddingTop() + mTopUnitHeight;

            if (i == 0 || i == yLinesCount - 1) {
                canvas.drawLine(start.x, start.y, stop.x, stop.y, paintLine);
            } else {
                int dotCount = 15;
                for (int j = 0; j < dotCount; j++) {
                    canvas.drawPoint(stop.x, stop.y + (yHeight * 1.0F / dotCount) * (j + 1), paintDot);
                }
            }
            // 绘制横坐标单位
            String drawText = yLineStr[i];
            Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
            float offsetY = ((fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 + fontMetrics.bottom) / 2;
            float baseLine = getHeight() - getPaddingBottom() - mBottomTextHeight / 2 + offsetY;
            float left = start.x - paintText.measureText(drawText) / 2;
            canvas.drawText(drawText, left, baseLine, paintText);
        }

    }

    // 绘制用于交互的图形
    private void drawViewTouch(Canvas canvas) {
        paintTouchLine.setStrokeWidth(ResUtils.getDimension(R.dimen.filter_adjust_line_touch));
        for (TouchLine touchLine : mTouchLineList) {
            Point startPoint = calculateStartPoint(touchLine);
            Point endPoint = calculateEndPoint(touchLine);
            Point touchPoint = calculateTouchPoint(touchLine);
            paintTouchLine.setStyle(Paint.Style.STROKE);
            if (mCurrentTouchLine == touchLine && mTouchType == 1 && touchLine.adjustAble) {
                paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_selected));
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paintTouchLine);
                paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
                paintTouchLine.setStyle(Paint.Style.FILL);
                canvas.drawCircle(endPoint.x, endPoint.y,
                        ResUtils.getDimension(R.dimen.filter_adjust_line_touch) / 2,
                        paintTouchLine);
                paintTouchLine.setStyle(Paint.Style.STROKE);
                canvas.drawLine(endPoint.x, endPoint.y, touchPoint.x, touchPoint.y, paintTouchLine);
            } else {
                if (mCurrentTouchLine == touchLine && mTouchType == 0)
                    paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
                else if (mTouchLineList.indexOf(touchLine) == mTouchLineList.size() - 1)
                    paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_selected));
                else
                    paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_unselected));
                Path path = new Path();
                path.moveTo(startPoint.x, startPoint.y);
                path.lineTo(endPoint.x, endPoint.y);
                if (touchLine.adjustAble)
                    path.lineTo(touchPoint.x, touchPoint.y);
                canvas.drawPath(path, paintTouchLine);
            }
            paintTouchLine.setStyle(Paint.Style.FILL);
            if (touchLine.adjustAble) {
                canvas.drawCircle(touchPoint.x, touchPoint.y,
                        ResUtils.getDimension(R.dimen.filter_adjust_line_point),
                        paintTouchLine);
            }
        }
    }


    // 绘制引导箭头
    private void drawViewGuide(Canvas canvas) {
        if (mTouchLineList == null || mTouchLineList.size() <= 0
                || !mTouchLineList.get(mTouchLineList.size() - 1).adjustAble)
            return;
        paintTouchLine.setPathEffect(new DashPathEffect(new float[]{6, 6}, 0));
        paintTouchLine.setStrokeWidth(2f);
        float guideLineSpace = ResUtils.getDimension(R.dimen.filter_guide_line_space);
        if (mCurrentTouchLine != null && mTouchType == 0) {
            paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
            paintTouchLine.setStyle(Paint.Style.STROKE);
            // 绘制虚直线
            float dLineStartX = (float) valueToX(MIN_VALUE);
            float dLineEndX = (float) valueToX(MAX_VALUE);
            float dLineY = calculateStartPoint(mCurrentTouchLine).y - guideLineSpace;
            canvas.drawLine(dLineStartX, dLineY, dLineEndX, dLineY, paintTouchLine);
            paintTouchLine.setStyle(Paint.Style.FILL);
            // 绘制左端箭头
            Path lArrow = new Path();
            lArrow.moveTo(dLineStartX - ResUtils.getDimension(R.dimen.filter_space_large), dLineY + 1);
            lArrow.lineTo(dLineStartX, dLineY + 1);
            lArrow.lineTo(dLineStartX, dLineY + 1 - ResUtils.getDimension(R.dimen.filter_space_min_more));
            canvas.drawPath(lArrow, paintTouchLine);
            // 绘制右端箭头
            Path rArrow = new Path();
            rArrow.moveTo(dLineEndX + ResUtils.getDimension(R.dimen.filter_space_large), dLineY + 1);
            rArrow.lineTo(dLineEndX, dLineY + 1);
            rArrow.lineTo(dLineEndX, dLineY + 1 - ResUtils.getDimension(R.dimen.filter_space_min_more));
            canvas.drawPath(rArrow, paintTouchLine);
        } else if (mCurrentTouchLine != null && mTouchType == 1) {
            Point endPoint = calculateEndPoint(mCurrentTouchLine);
            paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
            paintTouchLine.setStyle(Paint.Style.STROKE);
            // 绘制虚弧线
            RectF rectf = new RectF(endPoint.x - mAdjustLineLength - guideLineSpace,
                    endPoint.y - mAdjustLineLength - guideLineSpace,
                    endPoint.x + mAdjustLineLength + guideLineSpace,
                    endPoint.y + mAdjustLineLength + guideLineSpace);
            canvas.drawArc(rectf,
                    mCurrentTouchLine.direction == TouchLine.LEFT ? (float) MIN_ANGLE : 90 + (float) MIN_ANGLE,
                    (float) (MAX_ANGLE - MIN_ANGLE),
                    false, paintTouchLine);
            paintTouchLine.setStyle(Paint.Style.FILL);
            // 绘制左端箭头
            Path lArrow = new Path();
            double ldx = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(MAX_ANGLE)));
            double ldy = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(MAX_ANGLE)));
            float lx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + ldx : endPoint.x - ldx);
            float ly = (float) (endPoint.y + ldy);
            lArrow.moveTo(lx, ly);
            ldx = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                    * Math.cos(Math.toRadians(MAX_ANGLE)));
            ldy = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                    * Math.sin(Math.toRadians(MAX_ANGLE)));
            lx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + ldx : endPoint.x - ldx);
            ly = (float) (endPoint.y + ldy);
            lArrow.lineTo(lx, ly);
            ldx = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(MAX_ANGLE + 5)));
            ldy = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(MAX_ANGLE + 5)));
            lx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + ldx : endPoint.x - ldx);
            ly = (float) (endPoint.y + ldy);
            lArrow.lineTo(lx, ly);
            canvas.drawPath(lArrow, paintTouchLine);
            // 绘制右端箭头
            Path rArrow = new Path();
            double rdx = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(MIN_ANGLE)));
            double rdy = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(MIN_ANGLE)));
            float rx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + rdx : endPoint.x - rdx);
            float ry = (float) (endPoint.y + rdy);
            rArrow.moveTo(rx, ry);
            rdx = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                    * Math.cos(Math.toRadians(MIN_ANGLE)));
            rdy = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                    * Math.sin(Math.toRadians(MIN_ANGLE)));
            rx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + rdx : endPoint.x - rdx);
            ry = (float) (endPoint.y + rdy);
            rArrow.lineTo(rx, ry);
            rdx = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(MIN_ANGLE - 5)));
            rdy = (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(MIN_ANGLE - 5)));
            rx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + rdx : endPoint.x - rdx);
            ry = (float) (endPoint.y + rdy);
            rArrow.lineTo(rx, ry);
            canvas.drawPath(rArrow, paintTouchLine);
        } else {
            paintTouchLine.setColor(ResUtils.getColor(R.color.view_frequency_adjust_selected));
            paintTouchLine.setAlpha(mViewGuideAlpha);
            paintTouchLine.setStyle(Paint.Style.STROKE);
            TouchLine selectedTouchLine = mTouchLineList.get(mTouchLineList.size() - 1);
            Point startPoint = calculateStartPoint(selectedTouchLine);
            Point endPoint = calculateEndPoint(selectedTouchLine);
            // 绘制虚直线
            float dLineStartX = endPoint.x;
            dLineStartX = selectedTouchLine.direction == TouchLine.LEFT
                    ? dLineStartX - ResUtils.getDimension(R.dimen.filter_space_large)
                    : dLineStartX + ResUtils.getDimension(R.dimen.filter_space_large);
            float dLineEndX = xWidth / 6;
            dLineEndX = selectedTouchLine.direction == TouchLine.LEFT
                    ? dLineStartX - dLineEndX : dLineStartX + dLineEndX;
            float dLineY = startPoint.y - guideLineSpace;
            canvas.drawLine(dLineStartX, dLineY, dLineEndX, dLineY, paintTouchLine);
            paintTouchLine.setStyle(Paint.Style.FILL);
            // 绘制虚直线起始箭头
            if ((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.value < MAX_VALUE)
                    || (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.value > MIN_VALUE)) {
                Path sArrow = new Path();
                sArrow.moveTo(endPoint.x, dLineY + 1);
                sArrow.lineTo(dLineStartX, dLineY + 1);
                sArrow.lineTo(dLineStartX, dLineY + 1 - ResUtils.getDimension(R.dimen.filter_space_min_more));
                canvas.drawPath(sArrow, paintTouchLine);
            }
            // 绘制虚直线结束箭头
            if ((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.value > MIN_VALUE)
                    || (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.value < MAX_VALUE)) {
                Path eArrow = new Path();
                eArrow.moveTo(dLineEndX, dLineY + 1);
                eArrow.lineTo(dLineEndX, dLineY + 1 - ResUtils.getDimension(R.dimen.filter_space_min_more));
                eArrow.lineTo(dLineStartX > dLineEndX
                                ? dLineEndX - ResUtils.getDimension(R.dimen.filter_space_large)
                                : dLineEndX + ResUtils.getDimension(R.dimen.filter_space_large),
                        dLineY + 1);
                canvas.drawPath(eArrow, paintTouchLine);
            }
            // 绘制虚弧线
            if (selectedTouchLine.enableAngleAdjust) {
                paintTouchLine.setStyle(Paint.Style.STROKE);
                RectF rectf = new RectF(endPoint.x - mAdjustLineLength - guideLineSpace,
                        endPoint.y - mAdjustLineLength - guideLineSpace,
                        endPoint.x + mAdjustLineLength + guideLineSpace,
                        endPoint.y + mAdjustLineLength + guideLineSpace);
                float angleMin = (float) (selectedTouchLine.direction == TouchLine.LEFT ? MIN_ANGLE : MIN_ANGLE + 90);
                float angleMax = (float) (selectedTouchLine.direction == TouchLine.LEFT ? MAX_ANGLE : MAX_ANGLE + 90);
                float angle = (float) (selectedTouchLine.direction == TouchLine.LEFT ? selectedTouchLine.angle : 180 - selectedTouchLine.angle);
                float angleStart = angle - 15 < angleMin ? angleMin : angle - 15;
                float angleSweep = (angleMax - angle > 15 ? 15 : angleMax - angle) + (angle - angleMin > 15 ? 15 : angle - angleMin);
                canvas.drawArc(rectf, angleStart, angleSweep, false, paintTouchLine);

                paintTouchLine.setStyle(Paint.Style.FILL);
                // 绘制虚弧线左端箭头
                if ((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.angle < MAX_ANGLE)
                        || (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.angle > MIN_ANGLE)) {
                    Path lArrow = new Path();
                    lArrow.moveTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart + angleSweep)) + endPoint.x),
                            (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart + angleSweep)) + endPoint.y));
                    lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                                    * Math.cos(Math.toRadians(angleStart + angleSweep)) + endPoint.x),
                            (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                                    * Math.sin(Math.toRadians(angleStart + angleSweep)) + endPoint.y));
                    lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart + angleSweep + 5)) + endPoint.x),
                            (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart + angleSweep + 5)) + endPoint.y));
                    canvas.drawPath(lArrow, paintTouchLine);
                }
                // 绘制虚弧线右端箭头
                if ((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.angle > MIN_ANGLE)
                        || (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.angle < MAX_ANGLE)) {
                    Path lArrow = new Path();
                    lArrow.moveTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart)) + endPoint.x),
                            (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart)) + endPoint.y));
                    lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                                    * Math.cos(Math.toRadians(angleStart)) + endPoint.x),
                            (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.filter_space_min_more))
                                    * Math.sin(Math.toRadians(angleStart)) + endPoint.y));
                    lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart - 5)) + endPoint.x),
                            (float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart - 5)) + endPoint.y));
                    canvas.drawPath(lArrow, paintTouchLine);
                }
            }
        }
        paintTouchLine.setPathEffect(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchOn()) {
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            // 倒序遍历
            for (int i = mTouchLineList.size() - 1; i >= 0; i--) {
                TouchLine touchLine = mTouchLineList.get(i);
                Point startPoint = calculateStartPoint(touchLine);
                Point endPoint = calculateEndPoint(touchLine);
                // 判断触摸点是否在直线范围内
                if (Math.abs(startPoint.y - y) < mTouchRange) {
                    if (Math.max(startPoint.x, endPoint.x) + mTouchRange >= x
                            && Math.min(startPoint.x, endPoint.x) - mTouchRange <= x) {
                        mTouchType = 0;
                        mCurrentTouchLine = touchLine;
                        break;
                    }
                }
                // 判断触摸点是否在斜线范围内
                /**
                 * 直线方程 Ax + By + C = 0
                 * A = y2 - y1
                 * B = x1 - x2
                 * C = y1X2 - x1y2
                 *
                 * 点到直线距离公式 d = Math.abs((Ax + By + C)/Math.sqrt(A * A + B * B));
                 */
                Point touchPoint = calculateTouchPoint(touchLine);
                float x1 = endPoint.x;
                float x2 = touchPoint.x;
                float y1 = endPoint.y;
                float y2 = touchPoint.y;
                float a = y2 - y1;
                float b = x1 - x2;
                float c = y1 * x2 - x1 * y2;
                double d = Math.abs((a * x + b * y + c) / Math.sqrt(a * a + b * b));
                if (d > mTouchRange)
                    continue;
                // 判断是否在斜线的两个端点范围内
                if (x < Math.min(x1, x2) || x > Math.max(x1, x2)
                        || y < Math.min(y1, y2) || y > Math.max(y1, y2)) {
                    // 判断触碰范围是否是端点
                    if (Math.abs(Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1))) > mTouchRange
                            && Math.abs(Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2))) > mTouchRange)
                        continue;
                }
                // 判断是否在端点的触碰范围内
                if (Math.abs(Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2))) <= mTouchRange && touchLine.enableAngleAdjust)
                    mTouchType = 1;
                else
                    mTouchType = 0;
                mCurrentTouchLine = touchLine;
                break;
            }
            if (mTouchType != -1 && mCurrentTouchLine != null) {
                if (mOnTouchLineListener != null)
                    mOnTouchLineListener.onStartTouch(mCurrentTouchLine, mTouchType);
                if (mTouchLineList.get(mTouchLineList.size() - 1) != mCurrentTouchLine) {
                    mTouchLineList.remove(mCurrentTouchLine);
                    mTouchLineList.add(mCurrentTouchLine);
                    if (mOnTouchLineListener != null)
                        mOnTouchLineListener.onSwitchTouchLine(mTouchLineList.get(mTouchLineList.size() - 2), mCurrentTouchLine);
                }
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (mTouchType != -1 && mCurrentTouchLine != null && mOnTouchLineListener != null)
                mOnTouchLineListener.onStopTouch(mCurrentTouchLine, mTouchType);
            mTouchType = -1;
            mCurrentTouchLine = null;
        } else if (mTouchType != -1 && mCurrentTouchLine != null && mCurrentTouchLine.adjustAble) {
            Point endPoint = calculateEndPoint(mCurrentTouchLine);
            if (mTouchType == 0) {
                double oldValue = mCurrentTouchLine.value;
                double value = xToValue(endPoint.x + (x - mPreTouchX));
                mCurrentTouchLine.value = value < MIN_VALUE ? MIN_VALUE : value > MAX_VALUE ? MAX_VALUE : value;
                if (oldValue != mCurrentTouchLine.value && mOnTouchLineListener != null)
                    mOnTouchLineListener.onValueChanged(mCurrentTouchLine, oldValue, mCurrentTouchLine.value);
            } else if (mTouchType == 1) {
                // 判断触控范围是否超出了角度
                if (endPoint.y < y
                        && ((mCurrentTouchLine.direction == TouchLine.LEFT && endPoint.x < x)
                        || (mCurrentTouchLine.direction == TouchLine.RIGHT && endPoint.x > x))) {
                    double oldAngle = mCurrentTouchLine.angle;
                    double dx = Math.abs(x - endPoint.x);
                    double dy = Math.abs(y - endPoint.y);
                    double angle = Math.toDegrees(Math.atan(dy / dx));
                    mCurrentTouchLine.angle = angle > MAX_ANGLE ? MAX_ANGLE : angle < MIN_ANGLE ? MIN_ANGLE : angle;
                    if (oldAngle != mCurrentTouchLine.angle && mOnTouchLineListener != null)
                        mOnTouchLineListener.onAngleChanged(mCurrentTouchLine, oldAngle, mCurrentTouchLine.angle);
                }
            }
        }
        mPreTouchX = x;
        mPreTouchY = y;
        invalidate();
        return true;
    }

    public interface OnTouchLineListener {
        void onSwitchTouchLine(TouchLine oldTouchLine, TouchLine newTouchLine);

        void onValueChanged(TouchLine touchLine, double oldValue, double newValue);

        void onAngleChanged(TouchLine touchLine, double oldAngle, double newAngle);

        void onStartTouch(TouchLine touchLine, int touchType);

        void onStopTouch(TouchLine touchLine, int touchType);
    }


    private double valueToX(double value) {
        // 使用线性拟合将值转换成 X 坐标
        double a = 434.58f;
        double b = -1050f;
        double s = xWidth / 1400f;
        return (a * Math.log(value) + b) * s + getPaddingLeft();
    }

    private double xToValue(double x) {
        double a = 434.58f;
        double b = -1050f;
        double s = xWidth / 1400f;
        return Math.pow(Math.E, ((x - getPaddingLeft()) / s - b) / a);
    }


    private Point calculateStartPoint(@NonNull ViewFrequencyAdjust.TouchLine touchLine) {
        int y = getPaddingTop() + mTopUnitHeight;
        return new Point(touchLine.direction == ViewFrequencyAdjust.TouchLine.LEFT
                ? getPaddingLeft() + mLeftTextWidth
                : xWidth,
                y);
    }

    private Point calculateEndPoint(@NonNull ViewFrequencyAdjust.TouchLine touchLine) {
        int x = touchLine.adjustAble ? (int) valueToX(touchLine.value)
                : touchLine.direction == ViewFrequencyAdjust.TouchLine.LEFT
                ? xWidth
                : getPaddingLeft() + mLeftTextWidth;
        int y = getPaddingTop() + mTopUnitHeight;

        return new Point(x, y);

    }

    private Point calculateTouchPoint(@NonNull TouchLine touchLine) {
        Point touchPoint = new Point((int) (mAdjustLineLength * Math.cos(Math.toRadians(touchLine.angle)) + 0.5f),
                (int) (mAdjustLineLength * Math.sin(Math.toRadians(touchLine.angle)) + 0.5f));
        Point endPoint = calculateEndPoint(touchLine);
        touchPoint.x = touchLine.direction == TouchLine.LEFT
                ? endPoint.x + touchPoint.x
                : endPoint.x - touchPoint.x;
        touchPoint.y = endPoint.y + touchPoint.y;
        return touchPoint;
    }

    public void addTouchLine(@NonNull ViewFrequencyAdjust.TouchLine touchLine) {
        mTouchLineList.add(touchLine);
        invalidate();
        if (mOnTouchLineListener != null) {
            ViewFrequencyAdjust.TouchLine preSelectedTouchLine = mTouchLineList.size() == 1 ? null : mTouchLineList.get(mTouchLineList.size() - 2);
            mOnTouchLineListener.onSwitchTouchLine(preSelectedTouchLine, touchLine);
        }
    }

    public void setSelectedTouchLine(@NonNull ViewFrequencyAdjust.TouchLine touchLine) {
        ViewFrequencyAdjust.TouchLine preSelectedTouchLine = getSelectedTouchLine();
        if (preSelectedTouchLine != touchLine) {
            mTouchLineList.remove(touchLine);
            mTouchLineList.add(touchLine);
            invalidate();
            if (mOnTouchLineListener != null)
                mOnTouchLineListener.onSwitchTouchLine(preSelectedTouchLine, touchLine);
        }
    }


    public ViewFrequencyAdjust.TouchLine getSelectedTouchLine() {
        if (getTouchLineCount() == 0)
            return null;
        return mTouchLineList.get(mTouchLineList.size() - 1);
    }

    public int getTouchLineCount() {
        return mTouchLineList.size();
    }

    public List<TouchLine> getTouchAllLine() {
        return mTouchLineList;
    }

    public void setOnTouchLineListener(ViewFrequencyAdjust.OnTouchLineListener touchLineListener) {
        mOnTouchLineListener = touchLineListener;
    }

    public static class TouchLine {

        public static final int LEFT = 0;
        public static final int RIGHT = 1;

        @IntDef({LEFT, RIGHT})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Direction {
        }

        private double angle;
        private double value;
        @ViewFrequencyAdjust.TouchLine.Direction
        private int direction;
        private boolean adjustAble;
        private boolean enableAngleAdjust;

        public TouchLine() {
            adjustAble = true;
            enableAngleAdjust = true;
        }

        public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle < MIN_ANGLE ? MIN_ANGLE : angle > MAX_ANGLE ? MAX_ANGLE : angle;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value < MIN_VALUE ? MIN_VALUE : value > MAX_VALUE ? MAX_VALUE : value;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public boolean isAdjustAble() {
            return adjustAble;
        }

        public void setAdjustAble(boolean adjustAble) {
            this.adjustAble = adjustAble;
        }

        public boolean isEnableAngleAdjust() {
            return enableAngleAdjust;
        }

        public void setEnableAngleAdjust(boolean enableAngleAdjust) {
            this.enableAngleAdjust = enableAngleAdjust;
        }

        public static class Builder {
            private ViewFrequencyAdjust.TouchLine touchLine;

            public Builder() {
                touchLine = new ViewFrequencyAdjust.TouchLine();
                this.setValue(MIN_VALUE).setAngle(MIN_ANGLE).setDirection(LEFT);
            }

            public ViewFrequencyAdjust.TouchLine.Builder setValue(double value) {
                touchLine.setValue(value);
                return this;
            }

            public ViewFrequencyAdjust.TouchLine.Builder setAngle(double angle) {
                touchLine.setAngle(angle);
                return this;
            }

            public ViewFrequencyAdjust.TouchLine.Builder setDirection(@ViewFrequencyAdjust.TouchLine.Direction int direction) {
                touchLine.setDirection(direction);
                return this;
            }

            public ViewFrequencyAdjust.TouchLine.Builder setAdjustAble(boolean adjustAble) {
                touchLine.setAdjustAble(adjustAble);
                return this;
            }

            public ViewFrequencyAdjust.TouchLine.Builder setEnableAngleAdjust(boolean enableAngleAdjust) {
                touchLine.enableAngleAdjust = enableAngleAdjust;
                return this;
            }

            public ViewFrequencyAdjust.TouchLine create() {
                return touchLine;
            }
        }
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, getResources().getDisplayMetrics());
    }

}
