package com.flyaudio.soundeffect.comm.view.equalizer.seekbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.lib.utils.ConvertUtils;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.comm.SoundAdjustSeekBar;
import com.flyaudio.soundeffect.comm.view.layoutManager.NoScrollLinearLayoutManager;
import com.flyaudio.soundeffect.platform.util.CompareConfigUtils;
import com.flyaudio.soundeffect.platform.util.ConfigConstants;
import com.flyaudio.soundeffect.platform.util.ConfigUtils;
import com.flyaudio.soundeffect.platform.util.EQModeUtils;

import java.util.ArrayList;
import java.util.List;


public class EqualizerProfessionSeekBars extends RecyclerView {

    private Context context;
    private SeekbarsAdapter adapter;
    private List<DataBean> dataBeans;
    private SeekBarChangeListener seekBarChangeListener;
    private boolean adjustAble;
    private OnTouchListener interceptTouchListener;
    private OnScrollPercentageChangedListener onScrollPercentageChangedListener;

    // 用于显示值 EQ 的坐标
    private float touchX;
    private float touchY;
    private boolean isTouched=false;
    private int dbValue;
    private Paint paint;
    private int alpha;
    private ValueAnimator valueAnimator;

    public EqualizerProfessionSeekBars(Context context) {
        this(context, null, 0);
    }

    public EqualizerProfessionSeekBars(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqualizerProfessionSeekBars(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        setWillNotDraw(false);
        adjustAble = true;
        try {
            initData(context);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initData(Context context) {
        this.context = context;
        this.dataBeans = new ArrayList<>();
        this.adapter = new SeekbarsAdapter(this.context, dataBeans);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setTextSize(ConvertUtils.sp2px(ResUtils.getDimension(R.dimen.text_size_min_more)));
        this.touchX = -1;
        this.touchY = -1;
        this.alpha = 0xff;
        this.valueAnimator = ValueAnimator.ofInt(0xff, 0x00);
        this.valueAnimator.setDuration(200);
        this.valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator listener) {
                alpha = (int) listener.getAnimatedValue();
                invalidate();
            }
        });
        this.valueAnimator.start();
        this.interceptTouchListener = new OnTouchListener() {
            private float downX;
            private float downY;
            private boolean showIntercept;
            private float interceptOffset = ResUtils.getDimension(R.dimen.space_min_more);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = motionEvent.getX();
                    downY = motionEvent.getY();
                    showIntercept = true;
                    isTouched=true;
                } else if (showIntercept && Math.abs(motionEvent.getX() - downX) > interceptOffset) {
                    showIntercept = false;
                } else if (showIntercept && Math.abs(motionEvent.getY() - downY) > interceptOffset) {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        };

        int bandNum = ConfigUtils.equalizerBandNum();
        String[] titles = EQModeUtils.getTitles(bandNum);
        int[] defaultValues = new int[bandNum];
        for (int i = 0; i < titles.length; i++) {
            DataBean dataBean = new DataBean();
            dataBean.title = titles[i];
            dataBean.value = defaultValues[i];
            this.dataBeans.add(dataBean);
        }
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (onScrollPercentageChangedListener != null) {
                    onScrollPercentageChangedListener.onScrollPercentageChanged(getScrollPercentage());
                }
            }
        });
    }

    private void initView() {
        setAdapter(adapter);
        setHasFixedSize(true);
        int bandNum = ConfigUtils.equalizerBandNum();
        if (CompareConfigUtils.compareEqBandCount(bandNum, ConfigConstants.EQ_BAND_10))
            setLayoutManager(new NoScrollLinearLayoutManager(context, HORIZONTAL, false));
        else if (CompareConfigUtils.compareEqBandCount(bandNum, ConfigConstants.EQ_BAND_14) || CompareConfigUtils.compareEqBandCount(bandNum, ConfigConstants.EQ_BAND_31)||CompareConfigUtils.compareEqBandCount(bandNum, ConfigConstants.EQ_BAND_13))
            setLayoutManager(new LinearLayoutManager(context, HORIZONTAL, false));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!isTouched) return;

        String text = (dbValue > 0 ? "+" : "") + dbValue + " db";

        float textWidth = paint.measureText(text);
        float textOffsetX = ResUtils.getDimension(R.dimen.car_speaker_max_diffusion_diameter);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = Math.abs(fontMetrics.bottom - fontMetrics.top);
        float textOffsetY = fontMetrics.bottom;

        float textPaddingLeft = ResUtils.getDimension(R.dimen.space_min);
        float textPaddingRight = textPaddingLeft;
        float textPaddingTop = ResUtils.getDimension(R.dimen.car_speaker_min_diffusion_diameter);
        float textPaddingBottom = textPaddingTop;

        if (touchX - textOffsetX - textWidth - textPaddingRight < 0) {
            paint.setColor(ResUtils.getColor(R.color.theme_color_gray));
            paint.setAlpha(alpha);
            canvas.drawRoundRect(touchX + textOffsetX - textPaddingLeft,
                    touchY - textHeight / 2 - textOffsetY - textPaddingTop,
                    touchX + textOffsetX + textWidth + textPaddingRight,
                    touchY + textHeight / 2 - textOffsetY + textPaddingBottom,
                    textHeight, textHeight, paint);
            paint.setColor(ResUtils.getColor(R.color.text_color_white));
            paint.setAlpha(alpha);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(text, touchX + textOffsetX, touchY, paint);
        } else {
            paint.setColor(ResUtils.getColor(R.color.theme_color_gray));
            paint.setAlpha(alpha);
            canvas.drawRoundRect(touchX - textOffsetX - textWidth - textPaddingRight,
                    touchY - textHeight / 2 - textOffsetY - textPaddingTop,
                    touchX - textOffsetX + textPaddingLeft,
                    touchY + textHeight / 2 - textOffsetY + textPaddingBottom,
                    textHeight, textHeight, paint);
            paint.setColor(ResUtils.getColor(R.color.text_color_white));
            paint.setAlpha(alpha);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(text, touchX - textOffsetX, touchY, paint);
        }
    }

    public void setSeekBarChangeListener(SeekBarChangeListener listener) {
        seekBarChangeListener = listener;
//		adapter.refreshAdapter();
    }

    public void setProgress(int index, int value) {
        if (dataBeans.get(index).value == value)
            return;
        dataBeans.get(index).value = value;
        adapter.notifyItemChanged(index);
        if (seekBarChangeListener != null)
            seekBarChangeListener.onProgressChanged(index, value, false);
    }

    public int getProgress(int index) {
        return dataBeans.get(index).value;
    }

    public int[] getAllProgress() {
        int[] allProgress = new int[dataBeans.size()];
        for (int i = 0; i < allProgress.length; i++)
            allProgress[i] = getProgress(i);
        return allProgress;
    }

    public boolean isAdjustAble() {
        return adjustAble;
    }

    public void setAdjustAble(boolean adjustAble) {
        this.adjustAble = adjustAble;
        adapter.refreshAdapter();
    }

    public float getScrollPercentage() {
        return ((float) computeHorizontalScrollOffset()) / (computeHorizontalScrollRange() - computeHorizontalScrollExtent());
    }

    public void scrollToPercentage(float percentage) {
        float scrollPercentage = getScrollPercentage();
        float width = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        int dx = (int) ((percentage - scrollPercentage) * width + 0.5f);
        scrollBy(dx, 0);
    }

    public void setOnScrollPercentageChangedListener(OnScrollPercentageChangedListener listener) {
        onScrollPercentageChangedListener = listener;
    }

    private class SeekbarsAdapter extends RecyclerViewAdapter<DataBean> implements SoundAdjustSeekBar.SeekBarChangeListener {
        SoundAdjustSeekBar mSeekBarPrevious;

        SeekbarsAdapter(Context context, List<DataBean> datas) {
            super(context, datas);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.view_equalizer_profession_seekbars;
        }

        @Override
        public void onBindViewHolder(com.flyaudio.lib.adapter.Adapter.ViewHolder<DataBean> viewHolder) {
            DataBean data = getData(viewHolder.getItemPosition());
            EqualizerProfessionAdjustSeekBar seekBar = (EqualizerProfessionAdjustSeekBar) viewHolder.getContentView();

            ViewGroup.LayoutParams layoutParams = seekBar.getLayoutParams();
            if (CompareConfigUtils.compareEqBandCount(ConfigConstants.EQ_BAND_14))
                layoutParams.width = ConvertUtils.dp2px( 48.11F);
            seekBar.setLayoutParams(layoutParams);

            seekBar.setTitle(data.title);
            seekBar.setProgress(data.value);
            seekBar.setTag(viewHolder.getItemPosition());
            seekBar.setSeekBarChangeListener(SeekbarsAdapter.this);
            seekBar.setValueDetail(data.value);
            seekBar.setEnabled(adjustAble);
            seekBar.findViewById(R.id.equalizer_adjust_seek_bar).setOnTouchListener(interceptTouchListener);
        }

        @Override
        public void onProgressChanged(SoundAdjustSeekBar seekBar, int progress, boolean byTouch) {
            if (!byTouch)
                return;
            int index = (int) seekBar.getTag();
            dataBeans.get(index).value = progress;
            calculateTouchPoint(seekBar);
            if (seekBarChangeListener != null) {
                seekBarChangeListener.onProgressChanged(index, progress, true);
                if (mSeekBarPrevious!=null)
                    mSeekBarPrevious.setValueDetailNormal();
                seekBar.setValueDetailHighlight(progress);
                mSeekBarPrevious=seekBar;
            }


        }

        @Override
        public void onStartTrackingTouch(SoundAdjustSeekBar seekBar) {
            valueAnimator.cancel();
            alpha = 0xff;
            invalidate();
            if (seekBarChangeListener != null)
                seekBarChangeListener.onStartTrackingTouch((int) seekBar.getTag());
        }

        @Override
        public void onStopTrackingTouch(SoundAdjustSeekBar seekBar) {
            valueAnimator.start();
            if (seekBarChangeListener != null)
                seekBarChangeListener.onStopTrackingTouch((int) seekBar.getTag());
        }

        private void calculateTouchPoint(SoundAdjustSeekBar seekBar) {
            touchX = seekBar.getX() + seekBar.getWidth() / 2;
            View vSeekBar = seekBar.findViewById(R.id.equalizer_adjust_seek_bar);
            float height = vSeekBar.getWidth() - ResUtils.getDimension(R.dimen.car_speaker_max_diffusion_diameter) * 2;
            touchY = height - height * (seekBar.getProgress() - seekBar.getMin()) / (seekBar.getMax() - seekBar.getMin());
            touchY += (vSeekBar.getY() - height + ResUtils.getDimension(R.dimen.space_bitty));
            dbValue = seekBar.getProgress();
            invalidate();
        }

    }


    public interface SeekBarChangeListener {
        void onProgressChanged(int index, int progress, boolean byTouch);

        void onStartTrackingTouch(int index);

        void onStopTrackingTouch(int index);
    }

    public interface OnScrollPercentageChangedListener {
        void onScrollPercentageChanged(float scrollPercentage);
    }

    private class DataBean {
        String title;
        int value;
    }
}
