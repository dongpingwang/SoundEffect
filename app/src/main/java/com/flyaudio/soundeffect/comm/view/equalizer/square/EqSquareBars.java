package com.flyaudio.soundeffect.comm.view.equalizer.square;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 2020/3/511:59
 * email wangdongping@flyaudio.cn
 */
public class EqSquareBars extends RecyclerView implements RecyclerViewAdapter.OnItemClickListener {

    private Context context;
    private ProgressBar adapter;
    private List<DataBean> dataBeans;
    // 31段EQ分成了13个区间
    private int region;
    @Nullable
    private ProgressBarListener listener;
    private Paint paintText;
    private Paint paintLine;

    public EqSquareBars(Context context) {
        this(context, null, 0);
    }

    public EqSquareBars(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqSquareBars(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.setItemAnimator(null);
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
        this.adapter = new ProgressBar(this.context, dataBeans);
        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setTextSize(ResUtils.getDimension(R.dimen.eq_progress_font));
        paintLine = new Paint();
        paintLine.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
        paintLine.setStrokeWidth(ResUtils.getDimension(R.dimen.eq_progress_top_line));

        int[] freqs = BaseDiff.instance().bandFreqs();
        String[] titles = EQModeUtils.getTitles(freqs.length);
        int[] defaultValues = new int[freqs.length];
        for (int i = 0; i < titles.length; i++) {
            DataBean dataBean = new DataBean();
            dataBean.title = titles[i];
            dataBean.value = defaultValues[i];
            dataBean.freq = (int) freqs[i];
            this.dataBeans.add(dataBean);
        }

    }

    private void initView() {
        setAdapter(adapter);
        setHasFixedSize(true);
        setLayoutManager(new NoScrollLinearLayoutManager(context, HORIZONTAL, false));
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        float y = getPaddingTop() + ResUtils.getDimension(R.dimen.eq_progress_font_height);
        float item = ResUtils.getDimension(R.dimen.eq_progress_item_width);
        float x;
        float startX1, startY1, stopX1, stopY1;
        float startX2, startY2, stopX2, stopY2;
        startY1 = stopY1 = startY2 = stopY2 = y - 0.30F *  ResUtils.getDimension(R.dimen.eq_progress_font_height);
        if (region < 4) { // 低音
            startX1 = getPaddingLeft() + item * 0.3F;
            stopX1 = item * 1.5F;
            startX2 = item * 2.4F;
            stopX2 = item * 3.7F;
        } else if (region < 9) { // 中音
            startX1 = item * 4.3F;
            stopX1 = item * 6F;
            startX2 = item * 7F;
            stopX2 = item * 8.7F;
        } else { //高音
            startX1 = item * 9.2F;
            stopX1 = item * 10.5F;
            startX2 = item * 11.4F;
            stopX2 = item * 12.7F;
        }
        for (int i = 0; i < 3; i++) {
            x = item * (1.7F + 4.5F * i);
            String text = i == 0 ? ResUtils.getString(R.string.bass) : i == 1 ? ResUtils.getString(R.string.alto) : ResUtils.getString(R.string.high);
            if (i == 0 && region < 4) {
                paintText.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
            } else if (i == 1 && region >= 4 && region <= 8) {
                paintText.setColor(ResUtils.getColor(R.color.eq_progress_adjusting_color));
            } else if (i == 2 && region >= 9) {
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

    private class ProgressBar extends RecyclerViewAdapter<DataBean> {

        ProgressBar(Context context, List<DataBean> datas) {
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
        int[] gains = new int[getSize()];
        for (int i = 0; i < getSize(); i++) {
            gains[i] = adapter.getData(i).value;
        }
        return gains;
    }

    public void updateTitle(int frequency) {
        updateTitleByRegion(region, frequency);
    }

    public void updateRegion(int region) {
        this.region = region;
        adapter.refreshAdapter();
        invalidate();
    }

    public void updateTitles(int[] freqs) {
        for (int i = 0; i < adapter.getDatas().size(); i++) {
            adapter.getData(i).title = EQModeUtils.getFreq2Str(freqs[i]);
            adapter.getData(i).freq = freqs[i];
        }
        adapter.refreshAdapter();
    }

    public int getNextTitle() {
        return adapter.getData(this.region + 1).freq;
    }

    public int getPrevTitle() {
        return adapter.getData(this.region - 1).freq;
    }

    private void updateTitleByRegion(int region, int frequency) {
        adapter.getData(region).freq = frequency;
        adapter.getData(region).title = EQModeUtils.getFreq2Str(frequency);
        adapter.updateItem(region, adapter.getData(region));
    }

    public int getSize() {
        return adapter.getDatas().size();
    }

    public int[] getTitles() {
        int freqs[] = new int[adapter.getDatas().size()];
        for (int i = 0; i < adapter.getDatas().size(); i++) {
            freqs[i] = adapter.getData(i).freq;
        }
        return freqs;
    }

    public void setListener(@Nullable ProgressBarListener listener) {
        this.listener = listener;
    }

    private class DataBean {
        int value;
        String title;
        int freq;
    }

    public interface ProgressBarListener {
        void onRegionChange(int region);
    }
}
