package com.flyaudio.soundeffect.comm.view.equalizer.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;


public class EqualizerSimpleAdjustSeekBar extends EqualizerProfessionAdjustSeekBar {

	private Paint paint;
	private float scaleLong;
	private float scaleShort;
	private float scaleDistance;

	public EqualizerSimpleAdjustSeekBar(Context context) {
		super(context);
	}

	public EqualizerSimpleAdjustSeekBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public EqualizerSimpleAdjustSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context, AttributeSet attrs) {
		super.init(context, attrs);
		setWillNotDraw(false);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(ResUtils.getColor(R.color.text_color_white));
		paint.setStrokeWidth(ResUtils.getDimension(R.dimen.equalizer_simple_scale_thickness));

		scaleLong = ResUtils.getDimension(R.dimen.equalizer_simple_scale_long);
		scaleShort = ResUtils.getDimension(R.dimen.equalizer_simple_scale_short);
		scaleDistance = ResUtils.getDimension(R.dimen.equalizer_simple_scale_distance);

		adjustTitle.setTextColor(ResUtils.getColor(R.color.comm_text_color));
		adjustDetail.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtils.getDimension(R.dimen.equalizer_simple_text_size));
		adjustTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtils.getDimension(R.dimen.equalizer_simple_text_size));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST)
			width = (int) (scaleDistance + scaleLong * 2 + 0.5f);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int centerX = canvas.getWidth() / 2;
		int seekBarHeight = (int) (adjustSeek.getWidth() - ResUtils.getDimension(R.dimen.equalizer_seekbar_humb_offset) * 2);
		float seekBarY = adjustSeek.getY() - seekBarHeight + ResUtils.getDimension(R.dimen.equalizer_seekbar_humb_offset)
				- ResUtils.getDimension(R.dimen.space_bitty);
		float stepCount = (getMax() - getMin()) / getStep() / 2;
		float stepSpace = seekBarHeight / stepCount;
		for (int i = 0; i <= stepCount; i++) {
			float lineLength = (i == 0 || i == (int) stepCount || i == (int) stepCount / 2) ? scaleLong : scaleShort;
			canvas.drawLine(centerX - scaleDistance / 2,
					seekBarY + i * stepSpace,
					centerX - scaleDistance / 2 - lineLength,
					seekBarY + i * stepSpace, paint);
			canvas.drawLine(centerX + scaleDistance / 2,
					seekBarY + i * stepSpace,
					centerX + scaleDistance / 2 + lineLength,
					seekBarY + i * stepSpace, paint);
		}
	}
}
