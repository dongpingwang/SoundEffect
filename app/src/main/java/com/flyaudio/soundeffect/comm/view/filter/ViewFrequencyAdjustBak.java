package com.flyaudio.soundeffect.comm.view.filter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;

public class ViewFrequencyAdjustBak extends View {

	private static final int[] V_LINES = new int[]{20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400};
	private static final int[] POSITIONS = new int[]{20, 50, 100, 200, 400};
	private static final double MIN_VALUE = V_LINES[3];
	private static final double MAX_VALUE = V_LINES[9];
	private static final double MIN_ANGLE = 10;
	private static final double MAX_ANGLE = 90 - MIN_ANGLE;
	private static final long GUIDE_VIEW_SHOW_TIME = 600;

	private Paint mPaint;
	private int mTextHeight;
	private int mViewTop;
	private int mViewBottom;
	private int mViewLeft;
	private int mViewRight;
	private float mAdjustLineLength;
	private float mTouchRange;
	private List<TouchLine> mTouchLineList;
	private TouchLine mCurrentTouchLine;
	private int mTouchType;	// -1 代表没触碰，0 代表非端点，1 代表端点
	private float mPreTouchX;
	private float mPreTouchY;
	private int mViewGuideAlpha;
	private ValueAnimator mViewGuideAphaAnimator;
	private OnTouchLineListener mOnTouchLineListener;

	public ViewFrequencyAdjustBak(Context context) {
		this(context, null);
	}

	public ViewFrequencyAdjustBak(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewFrequencyAdjustBak(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, @Nullable AttributeSet attrs){
		// 关闭硬件加速，否则虚线绘制有问题
		setLayerType(LAYER_TYPE_SOFTWARE, mPaint);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

//		mTextHeight = (int) (ResUtils.getDimension(R.dimen.space_large_super) + 0.5f);
//		mViewTop = (int) (ResUtils.getDimension(R.dimen.space_min) + 0.5f);
//		mViewBottom = (int) (ResUtils.getDimension(R.dimen.space_min) + 0.5f);
//		mViewLeft = (int) (ResUtils.getDimension(R.dimen.space_middle) + 0.5f);
//		mViewRight = (int) (ResUtils.getDimension(R.dimen.space_middle) + 0.5f);

		mTextHeight = (int) (ResUtils.getDimension(R.dimen.view_frequency_adjust_bottom) + 0.5f);
		mViewTop = getPaddingTop();
		mViewBottom = getPaddingBottom();
		mViewLeft = getPaddingStart();
		mViewRight = getPaddingEnd();

		mAdjustLineLength = -1;
		mTouchRange = ResUtils.getDimension(R.dimen.space_large);
		mViewGuideAlpha = 0xFF;

		mTouchLineList = new LinkedList<>();
		mCurrentTouchLine = null;
		mTouchType = -1;

		mViewGuideAphaAnimator = ValueAnimator.ofInt(0xFF, 0x00);
		mViewGuideAphaAnimator.setDuration(GUIDE_VIEW_SHOW_TIME);
		mViewGuideAphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
		mViewGuideAphaAnimator.setRepeatCount(Animation.INFINITE);
		mViewGuideAphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				mViewGuideAlpha = (int) valueAnimator.getAnimatedValue();
				invalidate();
			}
		});
		mViewGuideAphaAnimator.start();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST)
			width = (int) ResUtils.getDimension(R.dimen.view_frequency_adjust_width);
		if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
			height = (int) ResUtils.getDimension(R.dimen.view_frequency_adjust_height);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mAdjustLineLength < 0)
			mAdjustLineLength = getHeight() / 2f;
		drawViewBackground(canvas);
		drawViewText(canvas);
		drawViewTouch(canvas);
		drawViewGuide(canvas);
	}

	// 绘制网格背景
	private void drawViewBackground(Canvas canvas){
		// 绘制背景颜色
		mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_bg));
		Rect rect = new Rect(mViewLeft, mViewTop, getWidth() - mViewRight, getHeight() - (mViewBottom + mTextHeight));
		canvas.drawRect(rect, mPaint);

		// 绘制横线
		mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_line));
		mPaint.setStrokeWidth(ResUtils.getDimension(R.dimen.view_frequency_adjust_line_h));
		for(int i = 1, j = (int) ((getHeight() - mViewTop - (mViewBottom + mTextHeight))/5f + 0.5); i < 5; i++) {
			int y = mViewTop + i * j;
			canvas.drawLine(mViewLeft, y, getWidth() - mViewRight, y, mPaint);
		}

		// 绘制竖线
		mPaint.setStrokeWidth(ResUtils.getDimension(R.dimen.view_frequency_adjust_line_v));
		for(int line : V_LINES) {
			float x = (float) valueToX(line);
			canvas.drawLine(x, mViewTop, x, getHeight() - (mViewBottom + mTextHeight), mPaint);
		}
	}

	// 绘制坐标文本
	private void drawViewText(Canvas canvas){
		mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_text));
		mPaint.setTextSize(ResUtils.getDimension(R.dimen.text_size_big));
		mPaint.setTextAlign(Paint.Align.CENTER);

		float baseline = (mTextHeight - (mPaint.descent() - mPaint.ascent())) / 2 - mPaint.ascent();
		float y = getHeight() - mTextHeight + baseline;

		for(int position : POSITIONS) {
			float x = (float) valueToX(position);
			canvas.drawText(String.valueOf(position), x, y, mPaint);
		}
	}

	// 绘制用于交互的图形
	private void drawViewTouch(Canvas canvas){
		mPaint.setStrokeWidth(ResUtils.getDimension(R.dimen.view_frequency_adjust_line_touch));
		for(TouchLine touchLine : mTouchLineList) {
			Point startPoint = calcuteStartPoint(touchLine);
			Point endPoint = calcuteEndPoint(touchLine);
			Point touchPoint = calcuteTouchPoint(touchLine);
			mPaint.setStyle(Paint.Style.STROKE);
			if(mCurrentTouchLine == touchLine && mTouchType == 1 && touchLine.adjustAble){
				mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_selected));
				canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, mPaint);
				mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(endPoint.x, endPoint.y,
						ResUtils.getDimension(R.dimen.view_frequency_adjust_line_touch)/2,
						mPaint);
				mPaint.setStyle(Paint.Style.STROKE);
				canvas.drawLine(endPoint.x, endPoint.y, touchPoint.x, touchPoint.y, mPaint);
			} else {
				if (mCurrentTouchLine == touchLine && mTouchType == 0)
					mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
				else if (mTouchLineList.indexOf(touchLine) == mTouchLineList.size() - 1)
					mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_selected));
				else
					mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_unselected));
				Path path = new Path();
				path.moveTo(startPoint.x, startPoint.y);
				path.lineTo(endPoint.x, endPoint.y);
				if(touchLine.adjustAble)
					path.lineTo(touchPoint.x, touchPoint.y);
				canvas.drawPath(path, mPaint);
			}
			mPaint.setStyle(Paint.Style.FILL);
			if(touchLine.adjustAble) {
				canvas.drawCircle(touchPoint.x, touchPoint.y,
						ResUtils.getDimension(R.dimen.view_frequency_adjust_line_point),
						mPaint);
			}
		}
	}

	// 绘制引导箭头
	private void drawViewGuide(Canvas canvas){
		if(mTouchLineList == null || mTouchLineList.size() <= 0
				|| !mTouchLineList.get(mTouchLineList.size()-1).adjustAble)
			return;
		mPaint.setPathEffect(new DashPathEffect(new float[]{6, 6}, 0));
		mPaint.setStrokeWidth(2f);
		float guideLineSpace = ResUtils.getDimension(R.dimen.car_speaker_max_diffusion_diameter);
		if(mCurrentTouchLine != null && mTouchType == 0){
			mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
			mPaint.setStyle(Paint.Style.STROKE);
			// 绘制虚直线
			float dLineStartX = (float) valueToX(MIN_VALUE);
			float dLineEndX = (float) valueToX(MAX_VALUE);
			float dLineY = calcuteStartPoint(mCurrentTouchLine).y - guideLineSpace;
			canvas.drawLine(dLineStartX, dLineY, dLineEndX, dLineY, mPaint);
			mPaint.setStyle(Paint.Style.FILL);
			// 绘制左端箭头
			Path lArrow = new Path();
			lArrow.moveTo(dLineStartX - ResUtils.getDimension(R.dimen.space_large), dLineY + 1);
			lArrow.lineTo(dLineStartX, dLineY + 1);
			lArrow.lineTo(dLineStartX, dLineY + 1 - ResUtils.getDimension(R.dimen.space_min_more));
			canvas.drawPath(lArrow, mPaint);
			// 绘制右端箭头
			Path rArrow = new Path();
			rArrow.moveTo(dLineEndX + ResUtils.getDimension(R.dimen.space_large), dLineY + 1);
			rArrow.lineTo(dLineEndX, dLineY + 1);
			rArrow.lineTo(dLineEndX, dLineY + 1 - ResUtils.getDimension(R.dimen.space_min_more));
			canvas.drawPath(rArrow, mPaint);
		} else if(mCurrentTouchLine != null && mTouchType == 1){
			Point endPoint = calcuteEndPoint(mCurrentTouchLine);
			mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_focus));
			mPaint.setStyle(Paint.Style.STROKE);
			// 绘制虚弧线
			RectF rectf = new RectF(endPoint.x-mAdjustLineLength-guideLineSpace,
					endPoint.y-mAdjustLineLength-guideLineSpace,
					endPoint.x+mAdjustLineLength+guideLineSpace,
					endPoint.y+mAdjustLineLength+guideLineSpace);
			canvas.drawArc(rectf,
					mCurrentTouchLine.direction == TouchLine.LEFT ? (float) MIN_ANGLE : 90+(float) MIN_ANGLE,
					(float)(MAX_ANGLE - MIN_ANGLE),
					false, mPaint);
			mPaint.setStyle(Paint.Style.FILL);
			// 绘制左端箭头
			Path lArrow = new Path();
			double ldx = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.cos(Math.toRadians(MAX_ANGLE)));
			double ldy = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.sin(Math.toRadians(MAX_ANGLE)));
			float lx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + ldx : endPoint.x - ldx);
			float ly = (float) (endPoint.y + ldy);
			lArrow.moveTo(lx, ly);
			ldx = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
					*Math.cos(Math.toRadians(MAX_ANGLE)));
			ldy = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
					*Math.sin(Math.toRadians(MAX_ANGLE)));
			lx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + ldx : endPoint.x - ldx);
			ly = (float) (endPoint.y + ldy);
			lArrow.lineTo(lx, ly);
			ldx = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.cos(Math.toRadians(MAX_ANGLE +5)));
			ldy = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.sin(Math.toRadians(MAX_ANGLE +5)));
			lx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + ldx : endPoint.x - ldx);
			ly = (float) (endPoint.y + ldy);
			lArrow.lineTo(lx, ly);
			canvas.drawPath(lArrow, mPaint);
			// 绘制右端箭头
			Path rArrow = new Path();
			double rdx = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.cos(Math.toRadians(MIN_ANGLE)));
			double rdy = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.sin(Math.toRadians(MIN_ANGLE)));
			float rx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + rdx : endPoint.x - rdx);
			float ry = (float) (endPoint.y + rdy);
			rArrow.moveTo(rx, ry);
			rdx = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
					*Math.cos(Math.toRadians(MIN_ANGLE)));
			rdy = (float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
					*Math.sin(Math.toRadians(MIN_ANGLE)));
			rx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + rdx : endPoint.x - rdx);
			ry = (float) (endPoint.y + rdy);
			rArrow.lineTo(rx, ry);
			rdx = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.cos(Math.toRadians(MIN_ANGLE -5)));
			rdy = (float) ((mAdjustLineLength + guideLineSpace - 1)*Math.sin(Math.toRadians(MIN_ANGLE -5)));
			rx = (float) (mCurrentTouchLine.direction == TouchLine.LEFT ? endPoint.x + rdx : endPoint.x - rdx);
			ry = (float) (endPoint.y + rdy);
			rArrow.lineTo(rx, ry);
			canvas.drawPath(rArrow, mPaint);
		} else {
			mPaint.setColor(ResUtils.getColor(R.color.view_frequency_adjust_selected));
			mPaint.setAlpha(mViewGuideAlpha);
			mPaint.setStyle(Paint.Style.STROKE);
			TouchLine selectedTouchLine = mTouchLineList.get(mTouchLineList.size()-1);
			Point startPoint = calcuteStartPoint(selectedTouchLine);
			Point endPoint = calcuteEndPoint(selectedTouchLine);
			// 绘制虚直线
			float dLineStartX = endPoint.x;
			dLineStartX = selectedTouchLine.direction == TouchLine.LEFT
					? dLineStartX - ResUtils.getDimension(R.dimen.space_large)
					: dLineStartX + ResUtils.getDimension(R.dimen.space_large);
			float dLineEndX = (getWidth() - mViewLeft - mViewRight)/6;
			dLineEndX = selectedTouchLine.direction == TouchLine.LEFT
					? dLineStartX - dLineEndX : dLineStartX + dLineEndX;
			float dLineY = startPoint.y - guideLineSpace;
			canvas.drawLine(dLineStartX, dLineY, dLineEndX, dLineY, mPaint);
			mPaint.setStyle(Paint.Style.FILL);
			// 绘制虚直线起始箭头
			if((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.value < MAX_VALUE)
					|| (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.value > MIN_VALUE)) {
				Path sArrow = new Path();
				sArrow.moveTo(endPoint.x, dLineY + 1);
				sArrow.lineTo(dLineStartX, dLineY + 1);
				sArrow.lineTo(dLineStartX, dLineY + 1 - ResUtils.getDimension(R.dimen.space_min_more));
				canvas.drawPath(sArrow, mPaint);
			}
			// 绘制虚直线结束箭头
			if((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.value > MIN_VALUE)
					|| (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.value < MAX_VALUE)) {
				Path eArrow = new Path();
				eArrow.moveTo(dLineEndX, dLineY + 1);
				eArrow.lineTo(dLineEndX, dLineY + 1 - ResUtils.getDimension(R.dimen.space_min_more));
				eArrow.lineTo(dLineStartX > dLineEndX
								? dLineEndX - ResUtils.getDimension(R.dimen.space_large)
								: dLineEndX + ResUtils.getDimension(R.dimen.space_large),
						dLineY + 1);
				canvas.drawPath(eArrow, mPaint);
			}
			// 绘制虚弧线
			if(selectedTouchLine.enableAngleAdjust) {
				mPaint.setStyle(Paint.Style.STROKE);
				RectF rectf = new RectF(endPoint.x - mAdjustLineLength - guideLineSpace,
						endPoint.y - mAdjustLineLength - guideLineSpace,
						endPoint.x + mAdjustLineLength + guideLineSpace,
						endPoint.y + mAdjustLineLength + guideLineSpace);
				float angleMin = (float) (selectedTouchLine.direction == TouchLine.LEFT ? MIN_ANGLE : MIN_ANGLE + 90);
				float angleMax = (float) (selectedTouchLine.direction == TouchLine.LEFT ? MAX_ANGLE : MAX_ANGLE + 90);
				float angle = (float) (selectedTouchLine.direction == TouchLine.LEFT ? selectedTouchLine.angle : 180 - selectedTouchLine.angle);
				float angleStart = angle - 15 < angleMin ? angleMin : angle - 15;
				float angleSweep = (angleMax - angle > 15 ? 15 : angleMax - angle) + (angle - angleMin > 15 ? 15 : angle - angleMin);
				canvas.drawArc(rectf, angleStart, angleSweep, false, mPaint);

				mPaint.setStyle(Paint.Style.FILL);
				// 绘制虚弧线左端箭头
				if ((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.angle < MAX_ANGLE)
						|| (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.angle > MIN_ANGLE)) {
					Path lArrow = new Path();
					lArrow.moveTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart + angleSweep)) + endPoint.x),
							(float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart + angleSweep)) + endPoint.y));
					lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
									* Math.cos(Math.toRadians(angleStart + angleSweep)) + endPoint.x),
							(float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
									* Math.sin(Math.toRadians(angleStart + angleSweep)) + endPoint.y));
					lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart + angleSweep + 5)) + endPoint.x),
							(float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart + angleSweep + 5)) + endPoint.y));
					canvas.drawPath(lArrow, mPaint);
				}
				// 绘制虚弧线右端箭头
				if ((selectedTouchLine.direction == TouchLine.LEFT && selectedTouchLine.angle > MIN_ANGLE)
						|| (selectedTouchLine.direction == TouchLine.RIGHT && selectedTouchLine.angle < MAX_ANGLE)) {
					Path lArrow = new Path();
					lArrow.moveTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart)) + endPoint.x),
							(float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart)) + endPoint.y));
					lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
									* Math.cos(Math.toRadians(angleStart)) + endPoint.x),
							(float) ((mAdjustLineLength + guideLineSpace - 1 + ResUtils.getDimension(R.dimen.space_min_more))
									* Math.sin(Math.toRadians(angleStart)) + endPoint.y));
					lArrow.lineTo((float) ((mAdjustLineLength + guideLineSpace - 1) * Math.cos(Math.toRadians(angleStart - 5)) + endPoint.x),
							(float) ((mAdjustLineLength + guideLineSpace - 1) * Math.sin(Math.toRadians(angleStart - 5)) + endPoint.y));
					canvas.drawPath(lArrow, mPaint);
				}
			}
		}
		mPaint.setPathEffect(null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		if(action == MotionEvent.ACTION_DOWN) {
			// 倒序遍历
			for(int i = mTouchLineList.size() - 1; i >= 0; i--){
				TouchLine touchLine = mTouchLineList.get(i);
				Point startPoint = calcuteStartPoint(touchLine);
				Point endPoint = calcuteEndPoint(touchLine);
				// 判断触摸点是否在直线范围内
				if(Math.abs(startPoint.y - y) < mTouchRange) {
					if(Math.max(startPoint.x, endPoint.x) + mTouchRange >= x
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
				Point touchPoint = calcuteTouchPoint(touchLine);
				float x1 = endPoint.x;
				float x2 = touchPoint.x;
				float y1 = endPoint.y;
				float y2 = touchPoint.y;
				float a = y2 - y1;
				float b = x1 - x2;
				float c = y1 * x2 - x1 * y2;
				double d = Math.abs((a*x + b*y + c)/Math.sqrt(a*a + b*b));
				if(d > mTouchRange)
					continue;
				// 判断是否在斜线的两个端点范围内
				if(x < Math.min(x1, x2) || x > Math.max(x1, x2)
						|| y < Math.min(y1, y2) || y > Math.max(y1, y2)) {
					// 判断触碰范围是否是端点
					if(Math.abs(Math.sqrt((x-x1) * (x-x1) + (y-y1) * (y-y1))) > mTouchRange
							&& Math.abs(Math.sqrt((x-x2) * (x-x2) + (y-y2) * (y-y2))) > mTouchRange)
						continue;
				}
				// 判断是否在端点的触碰范围内
				if(Math.abs(Math.sqrt((x-x2) * (x-x2) + (y-y2) * (y-y2))) <= mTouchRange && touchLine.enableAngleAdjust)
					mTouchType = 1;
				else
					mTouchType = 0;
				mCurrentTouchLine = touchLine;
				break;
			}
			if(mTouchType != -1 && mCurrentTouchLine != null) {
				if (mOnTouchLineListener != null)
					mOnTouchLineListener.onStartTouch(mCurrentTouchLine, mTouchType);
				if (mTouchLineList.get(mTouchLineList.size() - 1) != mCurrentTouchLine) {
					mTouchLineList.remove(mCurrentTouchLine);
					mTouchLineList.add(mCurrentTouchLine);
					if (mOnTouchLineListener != null)
						mOnTouchLineListener.onSwitchTouchLine(mTouchLineList.get(mTouchLineList.size() - 2), mCurrentTouchLine);
				}
			}
		} else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			if(mTouchType != -1 && mCurrentTouchLine != null && mOnTouchLineListener != null)
				mOnTouchLineListener.onStopTouch(mCurrentTouchLine, mTouchType);
			mTouchType = -1;
			mCurrentTouchLine = null;
		} else if(mTouchType != -1 && mCurrentTouchLine != null && mCurrentTouchLine.adjustAble){
			Point endPoint = calcuteEndPoint(mCurrentTouchLine);
			if(mTouchType == 0){
				double oldValue = mCurrentTouchLine.value;
				double value = xToValue(endPoint.x + (x - mPreTouchX));
				mCurrentTouchLine.value = value < MIN_VALUE ? MIN_VALUE : value > MAX_VALUE ? MAX_VALUE : value;
				if(oldValue != mCurrentTouchLine.value && mOnTouchLineListener != null)
					mOnTouchLineListener.onValueChanged(mCurrentTouchLine, oldValue, mCurrentTouchLine.value);
			} else if(mTouchType == 1){
				// 判断触控范围是否超出了角度
				if(endPoint.y < y
						&& ((mCurrentTouchLine.direction == TouchLine.LEFT  && endPoint.x < x)
						|| (mCurrentTouchLine.direction == TouchLine.RIGHT && endPoint.x > x))) {
					double oldAngle = mCurrentTouchLine.angle;
					double dx = Math.abs(x - endPoint.x);
					double dy = Math.abs(y - endPoint.y);
					double angle = Math.toDegrees(Math.atan(dy / dx));
					mCurrentTouchLine.angle = angle > MAX_ANGLE ? MAX_ANGLE : angle < MIN_ANGLE ? MIN_ANGLE : angle;
					if(oldAngle != mCurrentTouchLine.angle && mOnTouchLineListener != null)
						mOnTouchLineListener.onAngleChanged(mCurrentTouchLine, oldAngle, mCurrentTouchLine.angle);
				}
			}
		}
		mPreTouchX = x;
		mPreTouchY = y;
		invalidate();
		return true;
	}

	private double valueToX(double value){
		// 使用线性拟合将值转换成 X 坐标
		double a = 405.58f;
		double b = -1145f;
		double s = (getWidth()-mViewLeft-mViewRight) / 1400f;
		return (a * Math.log(value) + b) * s + mViewLeft;
	}

	private double xToValue(double x){
		double a = 405.58f;
		double b = -1145f;
		double s = (getWidth()-mViewLeft-mViewRight) / 1400f;
		return Math.pow(Math.E, ((x-mViewLeft)/s-b)/a);
	}

	private Point calcuteStartPoint(@NonNull TouchLine touchLine){
		return new Point(touchLine.direction == TouchLine.LEFT
				? mViewRight * 3
				: getWidth() - mViewRight * 3,
				mViewTop + (getHeight() - mViewTop - (mViewBottom + mTextHeight))/5);
	}

	private Point calcuteEndPoint(@NonNull TouchLine touchLine){
		int x = touchLine.adjustAble ? (int)valueToX(touchLine.value)
				: touchLine.direction == TouchLine.LEFT
				? getWidth() - mViewRight * 3
				: mViewRight * 3;
		int y = mViewTop + (getHeight() - mViewTop - (mViewBottom + mTextHeight))/5;
		return new Point(x, y);
	}

	private Point calcuteTouchPoint(@NonNull TouchLine touchLine){
		Point touchPoint = new Point((int)(mAdjustLineLength * Math.cos(Math.toRadians(touchLine.angle)) + 0.5f),
				(int)(mAdjustLineLength * Math.sin(Math.toRadians(touchLine.angle)) + 0.5f));
		Point endPoint = calcuteEndPoint(touchLine);
		touchPoint.x = touchLine.direction == TouchLine.LEFT
				? endPoint.x + touchPoint.x
				: endPoint.x - touchPoint.x;
		touchPoint.y = endPoint.y + touchPoint.y;
		return touchPoint;
	}

	public void updateView(){
		invalidate();
	}

	public void addTouchLine(@NonNull TouchLine touchLine){
		mTouchLineList.add(touchLine);
		invalidate();
		if(mOnTouchLineListener != null) {
			TouchLine preSelectedTouchLine = mTouchLineList.size() == 1 ? null : mTouchLineList.get(mTouchLineList.size()-2);
			mOnTouchLineListener.onSwitchTouchLine(preSelectedTouchLine, touchLine);
		}
	}

	public void removeTouchLine(@NonNull TouchLine touchLine){
		TouchLine preSelectedTouchLine = getSelectedTouchLine();
		mTouchLineList.remove(touchLine);
		invalidate();
		if(preSelectedTouchLine == touchLine && mOnTouchLineListener != null)
			mOnTouchLineListener.onSwitchTouchLine(preSelectedTouchLine, getSelectedTouchLine());
	}

	public void setSelectedTouchLine(@NonNull TouchLine touchLine){
		TouchLine preSelectedTouchLine = getSelectedTouchLine();
		if(preSelectedTouchLine != touchLine) {
			mTouchLineList.remove(touchLine);
			mTouchLineList.add(touchLine);
			invalidate();
			if(mOnTouchLineListener != null)
				mOnTouchLineListener.onSwitchTouchLine(preSelectedTouchLine, touchLine);
		}
	}

	public TouchLine getSelectedTouchLine(){
		if(getTouchLineCount() == 0)
			return null;
		return mTouchLineList.get(mTouchLineList.size()-1);
	}

	public int getTouchLineCount(){
		return mTouchLineList.size();
	}

	public List<TouchLine> getTouchAllLine(){
		return mTouchLineList;
	}

	public void setOnTouchLineListener(OnTouchLineListener touchLineListener){
		mOnTouchLineListener = touchLineListener;
	}

	public interface OnTouchLineListener{
		void onSwitchTouchLine(TouchLine oldTouchLine, TouchLine newTouchLine);
		void onValueChanged(TouchLine touchLine, double oldValue, double newValue);
		void onAngleChanged(TouchLine touchLine, double oldAngle, double newAngle);
		void onStartTouch(TouchLine touchLine, int touchType);
		void onStopTouch(TouchLine touchLine, int touchType);
	}

	public static class TouchLine {

		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		@IntDef({LEFT, RIGHT})
		@Retention(RetentionPolicy.SOURCE)
		public @interface Direction {}

		private double angle;
		private double value;
		@Direction private int direction;
		private boolean adjustAble;
		private boolean enableAngleAdjust;

		public TouchLine(){
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

		public static class Builder{
			private TouchLine touchLine;
			public Builder(){
				touchLine = new TouchLine();
				this.setValue(MIN_VALUE).setAngle(MIN_ANGLE).setDirection(LEFT);
			}
			public Builder setValue(double value){
				touchLine.setValue(value);
				return this;
			}
			public Builder setAngle(double angle){
				touchLine.setAngle(angle);
				return this;
			}
			public Builder setDirection(@Direction int direction){
				touchLine.setDirection(direction);
				return this;
			}
			public Builder setAdjustAble(boolean adjustAble) {
				touchLine.setAdjustAble(adjustAble);
				return this;
			}
			public Builder setEnableAngleAdjust(boolean enableAngleAdjust){
				touchLine.enableAngleAdjust = enableAngleAdjust;
				return this;
			}
			public TouchLine create(){
				return touchLine;
			}
		}
	}
}
