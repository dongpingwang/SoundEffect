package com.flyaudio.soundeffect.comm.view.eq;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.config.EffectCommUtils;
import com.flyaudio.soundeffect.comm.util.EqUtils;
import com.flyaudio.soundeffect.comm.view.NoScrollLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dongping Wang
 * @date 2020/3/511:59
 * email wangdongping@flyaudio.cn
 */
public class EqSquareBars extends RecyclerView implements RecyclerViewAdapter.OnItemClickListener {


    private static final int FREQUENCY_MIN = 20 - 1;
    private static final int FREQUENCY_MAX = 20000 + 1;

    private static final int REGION_BASS = 4;
    private static final int REGION_ALTO = 9;
    private static final int REGION_COUNT = 3;


    private ProgressAdapter adapter;
    private List<DataBean> dataBeans;
    /**
     * 31段EQ分成了13个区间，当前调节的区间
     */
    private int region;
    private ProgressBarListener listener;
    private Paint paintText;
    private Paint paintLine;

    public EqSquareBars(Context context) {
        this(context, null);
    }

    public EqSquareBars(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqSquareBars(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // 去掉条目更新动画
        this.setItemAnimator(null);
        try {
            initData(context);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initData(Context context) {
        this.dataBeans = new ArrayList<>();
        this.adapter = new ProgressAdapter(context, dataBeans);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setTextSize(ResUtils.getDimension(R.dimen.eq_progress_font));
        paintLine = new Paint();
        paintLine.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
        paintLine.setStrokeWidth(ResUtils.getDimension(R.dimen.eq_progress_top_line));

        int[] eqFrequencies = EffectCommUtils.getFrequencies();
        String[] titles = getResources().getStringArray(R.array.eq_titles_13);
        for (int i = 0; i < titles.length; i++) {
            DataBean dataBean = new DataBean();
            dataBean.title = titles[i];
            dataBean.freq = eqFrequencies[i];
            this.dataBeans.add(dataBean);
        }
    }

    private void initView() {
        setAdapter(adapter);
        setHasFixedSize(true);
        setLayoutManager(new NoScrollLinearLayoutManager(getContext(), HORIZONTAL, false));
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        float y = getPaddingTop() + ResUtils.getDimension(R.dimen.eq_progress_font_height);
        float item = ResUtils.getDimension(R.dimen.eq_progress_item_width);
        float x;
        float startX1, startY1, stopX1, stopY1;
        float startX2, startY2, stopX2, stopY2;
        startY1 = stopY1 = startY2 = stopY2 = y - 0.30F * ResUtils.getDimension(R.dimen.eq_progress_font_height);
        if (region < REGION_BASS) {
            // 低音
            startX1 = getPaddingLeft() + item * 0.3F;
            stopX1 = item * 1.5F;
            startX2 = item * 2.4F;
            stopX2 = item * 3.7F;
        } else if (region < REGION_ALTO) {
            // 中音
            startX1 = item * 4.3F;
            stopX1 = item * 6F;
            startX2 = item * 7F;
            stopX2 = item * 8.7F;
        } else {
            // 高音
            startX1 = item * 9.2F;
            stopX1 = item * 10.5F;
            startX2 = item * 11.4F;
            stopX2 = item * 12.7F;
        }
        for (int i = 0; i < REGION_COUNT; i++) {
            x = item * (1.7F + 4.5F * i);
            String text = i == 0 ? ResUtils.getString(R.string.bass) : i == 1 ? ResUtils.getString(R.string.alto) : ResUtils.getString(R.string.high);
            if (i == 0 && region < REGION_BASS) {
                paintText.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
            } else if (i == 1 && region >= REGION_BASS && region < REGION_ALTO) {
                paintText.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
            } else if (i == 2 && region >= REGION_ALTO) {
                paintText.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
            } else {
                paintText.setColor(ResUtils.getColor(R.color.text_color_white8));
            }
            c.drawText(text, x, y, paintText);
        }
        c.drawLine(startX1, startY1, stopX1, stopY1, paintLine);
        c.drawLine(startX2, startY2, stopX2, stopY2, paintLine);
    }

    @Override
    public void onItemClick(int region) {
        updateRegion(region);
        if (listener != null) {
            listener.onRegionChange(region);
        }
    }

    private class ProgressAdapter extends RecyclerViewAdapter<DataBean> {

        ProgressAdapter(Context context, List<DataBean> datas) {
            super(context, datas);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.view_eq_square_bars;
        }

        @Override
        public void onBindViewHolder(com.flyaudio.lib.adapter.Adapter.ViewHolder<DataBean> holder) {
            DataBean data = getData(holder.getItemPosition());
            EqSquareProgress progressBar = holder.getView(R.id.pb_progress);
            TextView tvTitle = holder.getView(R.id.tv_title);
            progressBar.setProgress(data.value);
            progressBar.setAdjusting(region == holder.getItemPosition());
            tvTitle.setText(data.title);
            setOnItemClickListener(EqSquareBars.this);

        }
    }

    public void updateProgress(int progress) {
        adapter.getData(region).value = progress;
        adapter.updateItem(region, adapter.getData(region));
    }

    public void updateProgesses(int[] gains) {
        for (int i = 0; i < adapter.getDatas().size(); i++) {
            adapter.getData(i).value = gains[i];
            adapter.updateItem(i, adapter.getData(i));
        }
    }

    public int[] getProgesses() {
        int[] gains = new int[dataBeans.size()];
        for (int i = 0; i < dataBeans.size(); i++) {
            gains[i] = adapter.getData(i).value;
        }
        return gains;
    }

    public int getProgress() {
        return adapter.getData(region).value;
    }

    public void updateTitle(int frequency) {
        updateTitleByRegion(region, frequency);
    }

    public void updateRegion(int region) {
        this.region = region;
        adapter.refreshAdapter();
        invalidate();
    }

    public int getRegion() {
        return region;
    }

    public int[] getTitles() {
        int[] frequencies = new int[adapter.getDatas().size()];
        for (int i = 0; i < adapter.getDatas().size(); i++) {
            frequencies[i] = adapter.getData(i).freq;
        }
        return frequencies;
    }

    public int getCurrentTitle() {
        return adapter.getData(this.region).freq;
    }

    public void updateTitles(int[] frequencies) {
        for (int i = 0; i < adapter.getDatas().size(); i++) {
            adapter.getData(i).title = EqUtils.getFreq2Str(frequencies[i]);
            adapter.getData(i).freq = frequencies[i];
        }
        adapter.refreshAdapter();
    }

    public int getNextTitle() {
        int freq = FREQUENCY_MAX;
        if (region < dataBeans.size() - 1) {
            freq = adapter.getData(region + 1).freq;
        }
        return freq;
    }

    public int getPrevTitle() {
        int freq = FREQUENCY_MIN;
        if (region > 0) {
            freq = adapter.getData(region - 1).freq;
        }
        return freq;
    }

    private void updateTitleByRegion(int region, int frequency) {
        adapter.getData(region).freq = frequency;
        adapter.getData(region).title = EqUtils.getFreq2Str(frequency);
        adapter.updateItem(region, adapter.getData(region));
    }

    public void setListener(@Nullable ProgressBarListener listener) {
        this.listener = listener;
    }

    private class DataBean {
        int value;
        int freq;
        String title;
    }

    public interface ProgressBarListener {
        /**
         * 点击条目时eq区间发生变化
         *
         * @param region 当前区间
         */
        void onRegionChange(int region);
    }
}
