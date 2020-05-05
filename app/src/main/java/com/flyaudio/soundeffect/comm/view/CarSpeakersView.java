package com.flyaudio.soundeffect.comm.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
public class CarSpeakersView extends ImageView implements ValueAnimator.AnimatorUpdateListener {

    private static final long ANIMATOR_TIME = 2000;

    private int carImgWidth;
    private int carImgHeight;
    private float[][] speakerPositions;

    private int minDiffusionCircleRadius;
    private int maxDiffusionCircleRadius;
    private ValueAnimator diffusionCircleValueAnimator;

    private int minGradientCircleRadius;
    private int maxGradientCircleRadius;
    private ValueAnimator gradientCircleValueAnimator;

    private Drawable speaker;
    private Paint paint;

    private int carSpeakers;

    public CarSpeakersView(Context context) {
        this(context, null);
    }

    public CarSpeakersView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarSpeakersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER);
        setImageResource(R.drawable.comm_car_img);

        Drawable carImg = getResources().getDrawable(R.drawable.comm_car_img, null);
        carImgWidth = carImg.getIntrinsicWidth();
        carImgHeight = carImg.getIntrinsicHeight();
        speakerPositions = new float[][]{
                {-carImgWidth / 2.8f, -carImgHeight / 4f},
                {carImgWidth / 2.8f, -carImgHeight / 4f},
                {-carImgWidth / 2.8f, carImgHeight / 4f},
                {carImgWidth / 2.8f, carImgHeight / 4f},
                {0, carImgHeight / 2.3f}
        };

        speaker = getResources().getDrawable(R.drawable.comm_speaker, null);

        int strokeWidth = 2;
        int speakerSize = Math.max(speaker.getIntrinsicWidth(), speaker.getIntrinsicHeight());
        speakerSize = (int) (Math.sqrt(speakerSize * speakerSize * 2) + 0.5f);

        minDiffusionCircleRadius = (int) ((speakerSize + getResources().getDimension(R.dimen.speaker_min_diffusion_diameter)) / 2f + 0.5f);
        maxDiffusionCircleRadius = (int) (minDiffusionCircleRadius + getResources().getDimension(R.dimen.speaker_max_diffusion_diameter) + 0.5f);

        minGradientCircleRadius = minDiffusionCircleRadius;
        maxGradientCircleRadius = (int) (minGradientCircleRadius + getResources().getDimension(R.dimen.speaker_max_gradient_diameter) + 0.5f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(getResources().getColor(R.color.theme_color, null));

        diffusionCircleValueAnimator = ValueAnimator.ofInt(minDiffusionCircleRadius, maxDiffusionCircleRadius);
        diffusionCircleValueAnimator.setDuration(ANIMATOR_TIME);
        diffusionCircleValueAnimator.setRepeatCount(Animation.INFINITE);
        diffusionCircleValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        diffusionCircleValueAnimator.setInterpolator(new LinearInterpolator());
        diffusionCircleValueAnimator.addUpdateListener(this);

        gradientCircleValueAnimator = ValueAnimator.ofInt(0xEE, 0x11);
        gradientCircleValueAnimator.setDuration(ANIMATOR_TIME);
        gradientCircleValueAnimator.setRepeatCount(Animation.INFINITE);
        gradientCircleValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        gradientCircleValueAnimator.setInterpolator(new LinearInterpolator());
        gradientCircleValueAnimator.addUpdateListener(this);

        diffusionCircleValueAnimator.start();
        gradientCircleValueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            width = (int) Math.max(carImgWidth, (speakerPositions[1][0] + maxDiffusionCircleRadius) * 2 + paint.getStrokeWidth());
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            height = (int) Math.max(carImgHeight, (speakerPositions[4][1] + maxGradientCircleRadius) * 2 + paint.getStrokeWidth());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float centerX = getWidth() * 1.0F / 2;
        float centerY = getHeight() * 1.0F / 2;
        for (int i = 0; i < speakerPositions.length; i++) {
            if (((carSpeakers >> i) & 0x01) != 1) {
                continue;
            }
            int x = (int) (centerX + speakerPositions[i][0] + 0.5f);
            int y = (int) (centerY + speakerPositions[i][1] + 0.5f);
            if (i != speakerPositions.length - 1) {
                drawDiffusionSpeaker(canvas, x, y);
            } else {
                drawGradientSpeaker(canvas, x, y);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (diffusionCircleValueAnimator.isRunning()) {
            diffusionCircleValueAnimator.cancel();
        }
        if (gradientCircleValueAnimator.isRunning()) {
            gradientCircleValueAnimator.cancel();
        }
    }

    private void drawDiffusionSpeaker(Canvas canvas, int centerX, int centerY) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAlpha(0xFF);
        canvas.drawCircle(centerX, centerY, minDiffusionCircleRadius, paint);

        int circleRadius = (int) diffusionCircleValueAnimator.getAnimatedValue();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(calculateAlpha(circleRadius));
        canvas.drawCircle(centerX, centerY, circleRadius, paint);

        circleRadius += (maxDiffusionCircleRadius - minDiffusionCircleRadius) / 2;
        circleRadius = minDiffusionCircleRadius + (circleRadius - minDiffusionCircleRadius) % (maxDiffusionCircleRadius - minDiffusionCircleRadius);
        paint.setAlpha(calculateAlpha(circleRadius));
        canvas.drawCircle(centerX, centerY, circleRadius, paint);

        int speakerW = speaker.getIntrinsicWidth();
        int speakerH = speaker.getIntrinsicHeight();
        speaker.setBounds(centerX - speakerW / 2, centerY - speakerH / 2, centerX + speakerW / 2, centerY + speakerH / 2);
        speaker.draw(canvas);
    }

    private int calculateAlpha(int radius) {
        if (radius <= minDiffusionCircleRadius) {
            return 0xff;
        }
        if (radius >= maxDiffusionCircleRadius) {
            return 0x00;
        }
        return (maxDiffusionCircleRadius - radius) * 0xff / (maxDiffusionCircleRadius - minDiffusionCircleRadius);
    }

    private void drawGradientSpeaker(Canvas canvas, int centerX, int centerY) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        paint.setAlpha((int) gradientCircleValueAnimator.getAnimatedValue());
        canvas.drawCircle(centerX, centerY, maxGradientCircleRadius, paint);
        paint.setAlpha(0xFF);
        canvas.drawCircle(centerX, centerY, minGradientCircleRadius, paint);

        int speakerW = speaker.getIntrinsicWidth();
        int speakerH = speaker.getIntrinsicHeight();
        speaker.setBounds(centerX - speakerW / 2, centerY - speakerH / 2, centerX + speakerW / 2, centerY + speakerH / 2);
        speaker.draw(canvas);
    }

    public void setDrawableColor(int color) {
        paint.setColor(color);
    }

    public void setCarSpeakers(int speakers) {
        carSpeakers = speakers;
        invalidate();
    }

    public int getCarSpeakers() {
        return carSpeakers;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidate();
    }
}
