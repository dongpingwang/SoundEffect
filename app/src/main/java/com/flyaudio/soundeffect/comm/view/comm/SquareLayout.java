package com.flyaudio.soundeffect.comm.view.comm;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.flyaudio.soundeffect.R;


public class SquareLayout extends FrameLayout {

	private int longSizeMode;

	public SquareLayout(Context context) {
		this(context, null);
	}

	public SquareLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout);
		longSizeMode = typedArray.getInt(R.styleable.SquareLayout_long_size_mode, 4);
		typedArray.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(longSizeMode == 1 || (longSizeMode == 3 && width > height) || (longSizeMode == 4 && width < height))
			widthMeasureSpec = heightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		else
			widthMeasureSpec = heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int getLongSizeMode() {
		return longSizeMode;
	}

	public void setLongSizeMode(int longSizeMode) {
		this.longSizeMode = longSizeMode;
	}
}
