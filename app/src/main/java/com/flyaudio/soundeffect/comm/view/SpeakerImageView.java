package com.flyaudio.soundeffect.comm.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class SpeakerImageView extends ImageView implements ValueAnimator.AnimatorUpdateListener{

    private static final long ANIMATOR_TIME = 2000;

    private int minDiffusionCircleRadius;
    private int maxDiffusionCircleRadius;
    private ValueAnimator diffusionCircleValueAnimator;

    private Drawable speaker;
    private Paint mPaint;

    public SpeakerImageView(Context context) {
        this(context,null,0);
    }

    public SpeakerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeakerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        speaker = getResources().getDrawable(R.drawable.comm_speaker, null);

        int strokeWidth = 2;
        int speakerSize = Math.max(speaker.getIntrinsicWidth(), speaker.getIntrinsicHeight());
        speakerSize = (int) (Math.sqrt(speakerSize*speakerSize*2) + 0.5f);

        minDiffusionCircleRadius = (int) ((speakerSize + getResources().getDimension(R.dimen.car_speaker_min_diffusion_diameter))/2f + 0.5f);
        maxDiffusionCircleRadius = (int) (minDiffusionCircleRadius + getResources().getDimension(R.dimen.car_speaker_max_diffusion_diameter) + 0.5f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(getResources().getColor(R.color.theme_color,null));

        diffusionCircleValueAnimator = ValueAnimator.ofInt(minDiffusionCircleRadius, maxDiffusionCircleRadius);
        diffusionCircleValueAnimator.setDuration(ANIMATOR_TIME);
        diffusionCircleValueAnimator.setRepeatCount(Animation.INFINITE);
        diffusionCircleValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        diffusionCircleValueAnimator.setInterpolator(new LinearInterpolator());
        diffusionCircleValueAnimator.addUpdateListener(this);

        diffusionCircleValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isEnabled()) {
            drawDiffusionSpeaker(canvas, getWidth()/2, getHeight()/2);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (diffusionCircleValueAnimator.isRunning()) {
            diffusionCircleValueAnimator.cancel();
        }
    }

    private void drawDiffusionSpeaker(Canvas canvas, int centerX, int centerY) {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAlpha(0xFF);
        canvas.drawCircle(centerX, centerY, minDiffusionCircleRadius, mPaint);

        int circleRadius = (int) diffusionCircleValueAnimator.getAnimatedValue();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAlpha(calculateAlpha(circleRadius));
        canvas.drawCircle(centerX, centerY, circleRadius, mPaint);

        circleRadius += (maxDiffusionCircleRadius - minDiffusionCircleRadius)/2;
        circleRadius = minDiffusionCircleRadius + (circleRadius - minDiffusionCircleRadius)%(maxDiffusionCircleRadius - minDiffusionCircleRadius);
        mPaint.setAlpha(calculateAlpha(circleRadius));
        canvas.drawCircle(centerX, centerY, circleRadius, mPaint);

        int speakerW = speaker.getIntrinsicWidth();
        int speakerH = speaker.getIntrinsicHeight();
        speaker.setBounds(centerX-speakerW/2, centerY-speakerH/2, centerX+speakerW/2, centerY+speakerH/2);
        speaker.draw(canvas);
    }

    private int calculateAlpha(int radius) {
        if(radius <= minDiffusionCircleRadius) {
            return 0xff;
        }
        if(radius >= maxDiffusionCircleRadius) {
            return 0x00;
        }
        return (maxDiffusionCircleRadius - radius) * 0xff / (maxDiffusionCircleRadius - minDiffusionCircleRadius);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        invalidate();
    }

    public void setDrawable(Drawable drawable){
        this.speaker = drawable;
        invalidate();
    }
}
