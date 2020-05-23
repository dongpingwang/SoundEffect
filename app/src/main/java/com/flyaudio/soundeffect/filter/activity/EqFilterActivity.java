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

/**
 * @author Dongping Wang
 * date 2020/4/25  23:04
 * email wangdongping@flyaudio.cn
 */
public class EqFilterActivity extends BaseActivity {

    private CommTitleBar titleBar;
    private NumberSelector filterTypeSelector;
    private CheckButton phaseCbn;
    private CheckButton hpfCbn;
    private ViewFrequencyAdjust frequencyAdjust;
    private CommAdjustButton btnAdjustFreq;
    private CommAdjustButton btnAdjustSlop;

    private List<ViewFrequencyAdjust.TouchLine> touchLines;
    private List<Integer> selectorTypeList;

    private EqFilterManager eqFilterManager;
    private SubwooferManager subwooferManager;


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
            }
            touchLines.add(touchLine);
            frequencyAdjust.addTouchLine(touchLine);
        }

        frequencyAdjust.setOnTouchLineListener(new ViewFrequencyAdjust.OnTouchLineListener() {
            @Override
            public void onSwitchTouchLine(ViewFrequencyAdjust.TouchLine oldTouchLine, ViewFrequencyAdjust.TouchLine newTouchLine) {

            }

            @Override
            public void onValueChanged(ViewFrequencyAdjust.TouchLine touchLine, double oldValue, double newValue) {


            }

            @Override
            public void onAngleChanged(ViewFrequencyAdjust.TouchLine touchLine, double oldAngle, double newAngle) {

            }

            @Override
            public void onStartTouch(ViewFrequencyAdjust.TouchLine touchLine, int touchType) {

            }

            @Override
            public void onStopTouch(ViewFrequencyAdjust.TouchLine touchLine, int touchType) {

            }
        });

    }

    private void initTypeSelector() {
        // 切换前排、后排、重低音喇叭时，是根据相应的string来进行区分的
        selectorTypeList = new ArrayList<>();
        selectorTypeList.add(R.string.eq_filter_type_front);
        selectorTypeList.add(R.string.eq_filter_type_back);
        selectorTypeList.add(R.string.eq_filter_type_subwoofer);

        filterTypeSelector = getView(R.id.eq_filter_type_selector);
        filterTypeSelector.setAdjustRange(0, touchLines.size() - 1);
        filterTypeSelector.setValueFormatter(new NumberSelector.ValueFormatter() {
            @Override
            public void valueFormat(@NonNull TextView textView, int value) {
                textView.setText(selectorTypeList.get(value));
            }
        });

    }

    private void initCheckButton() {
        phaseCbn = getView(R.id.eq_filter_phase_cbn);
        hpfCbn = getView(R.id.eq_filter_hpf_cbn);
        phaseCbn.setChecked(!subwooferManager.isSubwooferReverse());
        phaseCbn.setOnCheckedChangeListener(checkButtonListener);
        hpfCbn.setChecked(eqFilterManager.isFilterEnable(touchLines.indexOf(frequencyAdjust.getSelectedTouchLine())));
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
                eqFilterManager.saveFilterEnable(touchLines.indexOf(touchLine), isChecked);
                // TODO
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
