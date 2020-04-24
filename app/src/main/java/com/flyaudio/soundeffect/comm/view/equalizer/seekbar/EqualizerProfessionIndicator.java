package com.flyaudio.soundeffect.comm.view.equalizer.seekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;


public class EqualizerProfessionIndicator extends View {

	private float xPosition;
	private int bgColor;
	private int indicatorColor;
	private Paint paint;
	private float preTouchX;
	private boolean hasTouch;
	private OnPercentageChangedListener percentageChangedListener;

	public EqualizerProfessionIndicator(Context context) {
		this(context, null);
	}

	public EqualizerProfessionIndicator(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EqualizerProfessionIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, @Nullable AttributeSet attrs){
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EqualizerProfessionIndicator);
		bgColor = typedArray.getColor(R.styleable.EqualizerProfessionIndicator_backgroundColor,
				ResUtils.getColor(R.color.theme_color_gray));
		indicatorColor = typedArray.getColor(R.styleable.EqualizerProfessionIndicator_indicatorColor,
				ResUtils.getColor(R.color.comm_ripple_color));
		typedArray.recycle();

		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void setPercentage(float value) {
  		float prePosition = xPosition;
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		xPosition = width*2f/3f * value + width/6f;
		xPosition = (xPosition < width/6f) ? width/6f : (xPosition > width-width/6f) ? width-width/6f : xPosition;
		invalidate();
		if(prePosition != xPosition && percentageChangedListener != null)
			percentageChangedListener.onPercentageChanged(getPercentage());
	}

	public float getPercentage() {
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		return (xPosition - width/6f)/(width*2f/3f);
	}

	public void setOnPercentageChangedListener(OnPercentageChangedListener listener){
		percentageChangedListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setColor(bgColor);
		canvas.drawRoundRect(getPaddingLeft(), getPaddingTop(),
				getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(),
				canvas.getHeight(), canvas.getHeight(), paint);
		paint.setColor(indicatorColor);
		int width = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
		canvas.drawRoundRect(xPosition-width/6, getPaddingTop(),
				xPosition+width/6, getHeight() - getPaddingBottom(),
				canvas.getHeight(), canvas.getHeight(), paint);
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		if(event.getAction() == MotionEvent.ACTION_DOWN && event.getX() >= xPosition-width/6f && event.getX() <= xPosition+width/6f) {
			hasTouch = true;
			preTouchX = event.getX();
		}
		if(hasTouch) {
			float prePosition = xPosition;
			xPosition += (event.getX() - preTouchX);
			xPosition = (xPosition < width/6f) ? width/6f : (xPosition > width-width/6f) ? width-width/6f : xPosition;
			preTouchX = event.getX();
			invalidate();
			if(prePosition != xPosition && percentageChangedListener != null)
				percentageChangedListener.onPercentageChanged(getPercentage());
		}
		if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
			hasTouch = false;
		return true;
	}

	public interface OnPercentageChangedListener{
		void onPercentageChanged(float percentage);
	}
}
