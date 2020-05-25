package com.flyaudio.soundeffect.filter.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CheckButton;
import com.flyaudio.soundeffect.comm.view.CommAdjustButton;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.NumberSelector;
import com.flyaudio.soundeffect.comm.view.filter.ViewFrequencyAdjust;
import com.flyaudio.soundeffect.filter.logic.EqFilterDataLogic;
import com.flyaudio.soundeffect.filter.logic.EqFilterManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

import java.util.ArrayList;
import java.util.List;

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

    private List<ViewFrequencyAdjust.TouchLine> touchLines;
    private List<Integer> selectorTypeList;

    private EqFilterManager eqFilterManager;
    private SubwooferManager subwooferManager;
    @EqFilterManager.FilterChannel
    private int currentFilterChannel = -1;

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
        frequencyAdjust.setOnTouchLineListener(new ViewFrequencyAdjust.OnTouchLineListener() {
            @Override
            public void onSwitchTouchLine(ViewFrequencyAdjust.TouchLine oldTouchLine, ViewFrequencyAdjust.TouchLine newTouchLine) {
                updateCurrentFilter(touchLines.indexOf(newTouchLine));
            }

            @Override
            public void onValueChanged(ViewFrequencyAdjust.TouchLine touchLine, double oldValue, double newValue) {
                int value = EqFilterDataLogic.limitValue(touchLine.getValue(), touchLine.equals(touchLines.get(SUBWOOFER)));
                btnAdjustFreq.setValue(ResUtils.getString(R.string.eq_filter_hz_str, value));
            }

            @Override
            public void onAngleChanged(ViewFrequencyAdjust.TouchLine touchLine, double oldAngle, double newAngle) {
                btnAdjustSlop.setValue(ResUtils.getString(R.string.eq_filter_slope_str, (touchLine.isAdjustAble()) ? EqFilterDataLogic.angle2Slope(touchLine.getAngle()) : 0));
            }

            @Override
            public void onStartTouch(ViewFrequencyAdjust.TouchLine touchLine, int touchType) {

            }

            @Override
            public void onStopTouch(ViewFrequencyAdjust.TouchLine touchLine, int touchType) {
                int channel = touchLines.indexOf(touchLine);
                if (touchType == 0) {
                    double touchLineValue = touchLine.getValue();
                    int value = EqFilterDataLogic.limitValue(touchLineValue, touchLine == touchLines.get(SUBWOOFER));
                    if (value != touchLineValue) {
                        touchLine.setValue(value);
                        frequencyAdjust.invalidate();
                        onValueChanged(touchLine, touchLineValue, touchLine.getValue());
                    }
                    if (value != eqFilterManager.getFilterFreq(channel)) {
                        eqFilterManager.saveFilterFreq(channel, value);
                        eqFilterManager.setEqFilter(channel, value, EqFilterDataLogic.angle2Slope(touchLine.getAngle()));
                    }
                } else if (touchType == 1) {
                    double touchLineAngle = touchLine.getAngle();
                    int angle = EqFilterDataLogic.limitAngle(touchLineAngle);
                    if (angle != touchLineAngle) {
                        touchLine.setAngle(angle);
                        frequencyAdjust.invalidate();
                        onAngleChanged(touchLine, touchLineAngle, angle);
                    }
                    int slope = EqFilterDataLogic.angle2Slope(touchLine.getAngle());
                    if (slope != eqFilterManager.getFilterSlope(channel)) {
                        eqFilterManager.setEqFilter(channel, EqFilterDataLogic.limitValue(touchLine.getValue(), touchLine.equals(touchLines.get(SUBWOOFER))), slope);
                        eqFilterManager.saveFilterSlope(channel, slope);
                    }
                }
            }
        });

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
                    updateCurrentFilter(newNum);
                }
            }
        });
    }

    private void initCheckButton() {
        phaseCbn = getView(R.id.eq_filter_phase_cbn);
        hpfCbn = getView(R.id.eq_filter_hpf_cbn);
        int channel = touchLines.indexOf(frequencyAdjust.getSelectedTouchLine());
        phaseCbn.setVisibility(channel == SUBWOOFER ? View.VISIBLE : View.GONE);
        phaseCbn.setChecked(!subwooferManager.isSubwooferReverse());
        hpfCbn.setChecked(eqFilterManager.isFilterEnable(channel));
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
                int channel = touchLines.indexOf(touchLine);
                eqFilterManager.saveFilterEnable(channel, isChecked);
                eqFilterManager.setEqFilter(channel, touchLine.getValue(),
                        EqFilterDataLogic.angle2Slope(touchLine.getAngle()));
            }
        }
    };

    private void initAdjustButton() {
        btnAdjustFreq = getView(R.id.btn_group1);
        btnAdjustSlop = getView(R.id.btn_group2);
        btnAdjustFreq.setTitle(ResUtils.getString(R.string.frequency));
        btnAdjustSlop.setTitle(ResUtils.getString(R.string.slope));
        btnAdjustFreq.setListener(adjustListener);
        btnAdjustSlop.setListener(adjustListener);
    }

    private void updateCurrentFilter(int channel) {
        if (currentFilterChannel != channel) {
            frequencyAdjust.setSelectedTouchLine(touchLines.get(channel));
            typeSelector.setValue(channel);
            phaseCbn.setVisibility(channel == SUBWOOFER ? View.VISIBLE : View.INVISIBLE);
            phaseCbn.setChecked(!subwooferManager.isSubwooferReverse());
            hpfCbn.setChecked(eqFilterManager.isFilterEnable(channel));
            btnAdjustFreq.setValue(ResUtils.getString(R.string.eq_filter_hz_str,
                    eqFilterManager.getFilterFreq(channel)));
            if (channel == SUBWOOFER) {
                btnAdjustSlop.setValue(ResUtils.getString(R.string.none));
            } else {
                btnAdjustSlop.setValue(ResUtils.getString(R.string.eq_filter_slope_str,
                        eqFilterManager.getFilterSlope(channel)));
            }
            currentFilterChannel = channel;
            eqFilterManager.saveCurrentFilter(channel);
        }

    }


    private CommAdjustButton.AdjustListener adjustListener = new CommAdjustButton.AdjustListener() {
        @Override
        public void onAdjust(View btn, boolean up) {
            if (btn.equals(btnAdjustFreq)) {
                // 调节频率
            } else {
                // 调节斜率
            }
        }
    };
}
