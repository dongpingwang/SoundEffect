package com.flyaudio.soundeffect.filter.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CheckButton;
import com.flyaudio.soundeffect.comm.view.CommAdjustButton;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.NumberSelector;
import com.flyaudio.soundeffect.comm.view.filter.ViewFrequencyAdjust;
import com.flyaudio.soundeffect.filter.bean.EqFilterParam;
import com.flyaudio.soundeffect.filter.logic.EqFilterDataLogic;
import com.flyaudio.soundeffect.filter.logic.EqFilterManager;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

import java.util.ArrayList;
import java.util.List;

import static com.flyaudio.soundeffect.filter.logic.EqFilterManager.FilterChannel.FRONT_ROW;
import static com.flyaudio.soundeffect.filter.logic.EqFilterManager.FilterChannel.REAR_ROW;
import static com.flyaudio.soundeffect.filter.logic.EqFilterManager.FilterChannel.SUBWOOFER;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:04
 * email wangdongping@flyaudio.cn
 */
public class EqFilterActivity extends BaseActivity {

    private CommTitleBar titleBar;
    private NumberSelector typeSelector;
    private CheckButton phaseCbn;
    private CheckButton hpfCbn;
    private ViewFrequencyAdjust frequencyAdjust;
    private CommAdjustButton btnAdjustFreq;
    private CommAdjustButton btnAdjustSlop;
    private TextView tvCloseHint;


    private List<ViewFrequencyAdjust.TouchLine> touchLines;
    private List<Integer> selectorTypeList;

    private EqFilterManager eqFilterManager;
    private SubwooferManager subwooferManager;
    private EqFilterParam eqFilterParam;
    private boolean subwooferOn;
    private boolean backRowOn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_eq_filter;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initFrequencyAdjustView();
        initTypeSelector();
        initCheckButton();
        initAdjustButton();
        updateCurrentFilter(eqFilterManager.getCurrentFilter());
    }

    private void initData() {
        eqFilterManager = EqFilterManager.getInstance();
        subwooferManager = SubwooferManager.getInstance();
        subwooferOn = subwooferManager.isSubwooferOn();
        backRowOn = BackRowManager.getInstance().isBackRowOn();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.eq_filter));
        titleBar.setListener(new CommTitleBar.TitleBarActionListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onReset() {

            }
        });
    }

    private void initFrequencyAdjustView() {
        touchLines = new ArrayList<>();
        frequencyAdjust = getView(R.id.fsv_filter);
        for (int channel : EqFilterManager.FILTER_CHANNELS) {
            ViewFrequencyAdjust.TouchLine touchLine = new ViewFrequencyAdjust.TouchLine.Builder()
                    .setDirection(ViewFrequencyAdjust.TouchLine.RIGHT)
                    .setAdjustAble(eqFilterManager.isFilterEnable(channel))
                    .setValue(eqFilterManager.getFilterFreq(channel))
                    .setAngle(EqFilterDataLogic.slope2Angle(eqFilterManager.getFilterSlope(channel)))
                    .create();
            if (channel == EqFilterManager.FilterChannel.SUBWOOFER) {
                // 重低音不能调节斜率
                touchLine.setEnableAngleAdjust(false);
                touchLine.setDirection(ViewFrequencyAdjust.TouchLine.LEFT);
            }
            touchLines.add(touchLine);
            frequencyAdjust.addTouchLine(touchLine);
        }
    }

    private void initTypeSelector() {
        // 切换前排、后排、重低音喇叭时，是根据相应的string来进行区分的
        selectorTypeList = new ArrayList<>();
        selectorTypeList.add(R.string.eq_filter_type_front);
        selectorTypeList.add(R.string.eq_filter_type_back);
        selectorTypeList.add(R.string.eq_filter_type_subwoofer);

        typeSelector = getView(R.id.eq_filter_type_selector);
        typeSelector.setAdjustRange(0, touchLines.size() - 1);
        typeSelector.setValueFormatter(new NumberSelector.ValueFormatter() {
            @Override
            public void valueFormat(@NonNull TextView textView, int value) {
                textView.setText(selectorTypeList.get(value));
            }
        });
        typeSelector.setOnNumberChangedListener(new NumberSelector.OnNumberChangedListener() {
            @Override
            public void onNumberChanged(int oldNum, int newNum, boolean byTouch) {
                if (byTouch) {
                    eqFilterParam = null;
                    updateCurrentFilter(newNum);
                }
            }
        });
    }

    private void initCheckButton() {
        phaseCbn = getView(R.id.eq_filter_phase_cbn);
        hpfCbn = getView(R.id.eq_filter_hpf_cbn);
        phaseCbn.setOnCheckedChangeListener(checkButtonListener);
        hpfCbn.setOnCheckedChangeListener(checkButtonListener);
    }

    private CheckButton.OnCheckedChangeListener checkButtonListener = new CheckButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CheckButton checkBtn, boolean isChecked) {
            if (checkBtn.equals(phaseCbn)) {
                subwooferManager.setSubooferReverse(!isChecked);
                subwooferManager.saveSubwooferReverse(!isChecked);
            } else {
                ViewFrequencyAdjust.TouchLine touchLine = frequencyAdjust.getSelectedTouchLine();
                touchLine.setAdjustAble(isChecked);
                frequencyAdjust.invalidate();
                eqFilterParam.enable = isChecked;
                eqFilterManager.saveFilterEnable(eqFilterParam.channel, isChecked);
                updateCurrentFilter(eqFilterParam.channel);
            }
        }
    };

    private void initAdjustButton() {
        btnAdjustFreq = getView(R.id.btn_group1);
        btnAdjustSlop = getView(R.id.btn_group2);
        tvCloseHint = getView(R.id.tv_filter_close_hint);
        btnAdjustFreq.setTitle(ResUtils.getString(R.string.frequency));
        btnAdjustSlop.setTitle(ResUtils.getString(R.string.slope));
        btnAdjustFreq.setListener(adjustListener);
        btnAdjustSlop.setListener(adjustListener);
    }

    private void updateCurrentFilter(int channel) {
        if (eqFilterParam == null) {
            eqFilterParam = new EqFilterParam();
            eqFilterParam.channel = channel;
            eqFilterParam.enable = eqFilterManager.isFilterEnable(channel);
            eqFilterParam.freq = eqFilterManager.getFilterFreq(channel);
            eqFilterParam.slope = eqFilterManager.getFilterSlope(channel);
        } else {
            if (eqFilterParam.channel != channel) {
                eqFilterParam.channel = channel;
                eqFilterManager.saveCurrentFilter(channel);
            }
            Logger.d("updateCurrentFilter:" + eqFilterParam.toString());
            eqFilterManager.setEqFilter(eqFilterParam.channel, eqFilterParam.freq, eqFilterParam.slope, eqFilterParam.enable);
        }
        ViewFrequencyAdjust.TouchLine touchLine = touchLines.get(channel);
        touchLine.setAdjustAble(eqFilterParam.enable);
        touchLine.setValue(EqFilterDataLogic.limitValue(eqFilterParam.freq, isSubwoofer()));
        touchLine.setAngle(EqFilterDataLogic.limitAngle(EqFilterDataLogic.slope2Angle(eqFilterParam.slope)));
        frequencyAdjust.setSelectedTouchLine(touchLine);

        typeSelector.setValue(channel);
        phaseCbn.setChecked(!subwooferManager.isSubwooferReverse());
        hpfCbn.setChecked(eqFilterParam.enable);
        btnAdjustFreq.setValue(ResUtils.getString(R.string.eq_filter_hz_str, eqFilterParam.freq));
        if (isSubwoofer()) {
            btnAdjustSlop.setValue(ResUtils.getString(R.string.none));
        } else {
            btnAdjustSlop.setValue(ResUtils.getString(R.string.eq_filter_slope_str, eqFilterParam.slope));
        }
        updateUiVisibility();
    }


    private void updateUiVisibility() {
        boolean disable = !eqFilterParam.enable;
        boolean closeWhenOnChannelSame = (eqFilterParam.channel != FRONT_ROW) && (!subwooferOn && isSubwoofer() || !backRowOn && !isSubwoofer());
        frequencyAdjust.setVisibility(disable || closeWhenOnChannelSame ? View.GONE : View.VISIBLE);
        btnAdjustFreq.setVisibility(disable || closeWhenOnChannelSame ? View.GONE : View.VISIBLE);
        btnAdjustSlop.setVisibility(disable || closeWhenOnChannelSame ? View.GONE : View.VISIBLE);
        tvCloseHint.setVisibility(disable || closeWhenOnChannelSame ? View.VISIBLE : View.GONE);
        titleBar.updateResetVisibility(!closeWhenOnChannelSame);
        phaseCbn.setVisibility(closeWhenOnChannelSame ? View.GONE : isSubwoofer() ? View.VISIBLE : View.GONE);
        hpfCbn.setVisibility(closeWhenOnChannelSame ? View.GONE : View.VISIBLE);

        if (!eqFilterParam.enable) {
            String hintText;
            if (eqFilterParam.channel == FRONT_ROW) {
                hintText = ResUtils.getString(R.string.front_row_high_filter_has_closed);
            } else if (eqFilterParam.channel == REAR_ROW) {
                hintText = ResUtils.getString(R.string.back_row_high_filter_has_closed);
            } else {
                hintText = ResUtils.getString(R.string.suboowfer_low_filter_has_closed);
            }
            tvCloseHint.setText(hintText);
        }
        if (eqFilterParam.channel != FRONT_ROW) {
            if (closeWhenOnChannelSame) {
                String hintText;
                if (!subwooferOn && !backRowOn) {
                    hintText = ResUtils.getString(R.string.suboowfer_and_back_row_have_closed_in_trumpet_setting);
                } else if (!subwooferOn) {
                    hintText = ResUtils.getString(R.string.suboowfer_has_closed_in_trumpet_setting);
                } else {
                    hintText = ResUtils.getString(R.string.back_row_has_closed_in_trumpet_setting);
                }
                tvCloseHint.setText(hintText);
            }
        }

    }

    private CommAdjustButton.AdjustListener adjustListener = new CommAdjustButton.AdjustListener() {
        @Override
        public void onAdjust(View btn, boolean up) {
            if (btn.equals(btnAdjustFreq)) {
                // 调节频率
                int freq;
                if (isSubwoofer()) {
                    freq = EqFilterDataLogic.getLpf(eqFilterParam.freq, up);
                } else {
                    freq = EqFilterDataLogic.getHpf(eqFilterParam.freq, up);
                }
                if (freq != eqFilterParam.freq) {
                    eqFilterParam.freq = freq;
                    eqFilterManager.saveFilterFreq(eqFilterParam.channel, freq);
                    updateCurrentFilter(eqFilterParam.channel);
                }
            } else {
                // 调节斜率
                int slope = EqFilterDataLogic.getSlope(eqFilterParam.slope, up);
                if (slope != eqFilterParam.slope) {
                    eqFilterParam.slope = slope;
                    eqFilterManager.saveFilterSlope(eqFilterParam.channel, slope);
                    updateCurrentFilter(eqFilterParam.channel);
                }
            }
        }
    };

    private boolean isSubwoofer() {
        return eqFilterParam != null && eqFilterParam.channel == SUBWOOFER;
    }
}
