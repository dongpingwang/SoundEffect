package com.flyaudio.soundeffect.equalizer.activity;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.util.EqUtils;
import com.flyaudio.soundeffect.comm.view.CommAdjustButton;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.CommVerticalAdjustButton;
import com.flyaudio.soundeffect.comm.view.eq.EqSquareBars;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.equalizer.logic.EqRegionDataLogic;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class EqAdjustActivity extends BaseActivity {

    public static final String INTENT_MODE_NAME = "mode_name";
    private static final String[] EQ_VALUE_DESCRIPTION = new String[]{ResUtils.getString(R.string.wide),
            ResUtils.getString(R.string.middle), ResUtils.getString(R.string.narrow)};

    private CommTitleBar titleBar;
    private EqSquareBars eqSquareBars;
    private CommVerticalAdjustButton btnGain;
    private CommAdjustButton btnFreq, btnEqValue;
    private EqMode eqMode;
    private EqManager eqManager;
    private EqDataBean eqDataBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_eq_adjust;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initSquareBars();
        initAdjustBtn();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(eqMode.getName());
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

    private void initSquareBars() {
        eqSquareBars = getView(R.id.eq_bars);
        eqSquareBars.setListener(new EqSquareBars.ProgressBarListener() {
            @Override
            public void onRegionChange(int region) {
                eqManager.saveLastAdjustIndex(eqMode.getId(), region);
            }
        });
        // 恢复上次调节
        eqSquareBars.updateProgesses(eqDataBean.gains);
        eqSquareBars.updateTitles(eqDataBean.frequencies);
        int lastAdjustIndex = eqManager.getLastAdjustIndex(eqMode.getId());
        eqSquareBars.updateRegion(lastAdjustIndex);
    }

    private void initAdjustBtn() {
        btnGain = getView(R.id.btn_group_eq);
        btnFreq = getView(R.id.btn_group1);
        btnEqValue = getView(R.id.btn_group2);
        btnFreq.setTitle(ResUtils.getString(R.string.frequency));
        btnEqValue.setTitle(ResUtils.getString(R.string.q_value_adjust));
        // 调节增益
        btnGain.setListener(new CommVerticalAdjustButton.AdjustListener() {
            @Override
            public void onUp() {
                int progress = EqRegionDataLogic.getGainUp(eqSquareBars.getProgress());
                eqSquareBars.updateProgress(progress);
                btnGain.setValue(progress + ResUtils.getString(R.string.gain_unit));
                eqDataBean.gains[eqSquareBars.getRegion()] = progress;
            }

            @Override
            public void onDown() {
                int progress = EqRegionDataLogic.getGainDown(eqSquareBars.getProgress());
                eqSquareBars.updateProgress(progress);
                btnGain.setValue(progress + ResUtils.getString(R.string.gain_unit));
                eqDataBean.gains[eqSquareBars.getRegion()] = progress;
            }
        });

        // 调节频率
        btnFreq.setListener(new CommAdjustButton.AdjustListener() {
            @Override
            public void onUp() {
                int freq = EqRegionDataLogic.getFreqUp(eqSquareBars.getRegion(), eqSquareBars.getCurrentTitle(), eqSquareBars.getPrevTitle(), eqSquareBars.getNextTitle());
                eqSquareBars.updateTitle(freq);
                btnFreq.setValue(EqUtils.getFreq2Str(freq));
                eqDataBean.frequencies[eqSquareBars.getRegion()] = freq;
            }

            @Override
            public void onDown() {
                int freq = EqRegionDataLogic.getFreqDown(eqSquareBars.getRegion(), eqSquareBars.getCurrentTitle(), eqSquareBars.getPrevTitle(), eqSquareBars.getNextTitle());
                eqSquareBars.updateTitle(freq);
                btnFreq.setValue(EqUtils.getFreq2Str(freq));
                eqDataBean.frequencies[eqSquareBars.getRegion()] = freq;
            }
        });

        // 调节q值
        btnEqValue.setListener(new CommAdjustButton.AdjustListener() {
            @Override
            public void onUp() {
                double eqValue = EqRegionDataLogic.getEqValue(eqDataBean.qValues[eqSquareBars.getRegion()], true);
                eqDataBean.qValues[eqSquareBars.getRegion()] = eqValue;
                btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqValue)]);
            }

            @Override
            public void onDown() {
                double eqValue = EqRegionDataLogic.getEqValue(eqDataBean.qValues[eqSquareBars.getRegion()], false);
                eqDataBean.qValues[eqSquareBars.getRegion()] = eqValue;
                btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqValue)]);
            }
        });
    }


    private void initData() {
        eqMode = getIntent().getParcelableExtra(INTENT_MODE_NAME);
        // 解析xml
        EqRegionDataLogic.getEqRegions();

        eqManager = EqManager.getInstance();
        eqDataBean = eqManager.getEqModeData(eqMode.getId());
    }


}
