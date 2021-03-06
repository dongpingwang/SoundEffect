package com.flyaudio.soundeffect.equalizer.activity;

import android.view.View;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.dialog.ResetDialog;
import com.flyaudio.soundeffect.comm.util.EqUtils;
import com.flyaudio.soundeffect.comm.view.CommAdjustButton;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.CommVerticalAdjustButton;
import com.flyaudio.soundeffect.comm.view.eq.EqSquareBars;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;
import com.flyaudio.soundeffect.equalizer.dialog.EqDataDetailDialog;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.equalizer.logic.EqRegionDataLogic;
import com.flyaudio.soundeffect.equalizer.util.TestUtils;

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
    private ResetDialog resetDialog;
    private EqDataDetailDialog eqDataDetailDialog;
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
        test();
    }

    private void initData() {
        eqMode = getIntent().getParcelableExtra(INTENT_MODE_NAME);
        // 解析xml
        EqRegionDataLogic.getEqRegions();

        eqManager = EqManager.getInstance();
        eqDataBean = eqManager.getEqModeData(eqMode.getId());
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
                if (resetDialog == null) {
                    resetDialog = new ResetDialog(context());
                }
                resetDialog.show();
                resetDialog.setListener(new ResetDialog.ResetListener() {
                    @Override
                    public void onReset() {
                        eqDataBean = eqManager.getDefaultEqModeData(eqMode.getId());
                        eqManager.saveEqModeData(eqMode.getId(), eqDataBean);
                        updateAllFreqEq();
                        updateCurrentFreqEq();
                        EffectManager.getInstance().setAllEq();
                        onCancel();
                    }

                    @Override
                    public void onCancel() {
                        resetDialog.cancel();
                    }
                });
            }
        });
    }

    private void initSquareBars() {
        eqSquareBars = getView(R.id.eq_bars);
        eqSquareBars.setListener(new EqSquareBars.ProgressBarListener() {
            @Override
            public void onRegionChange(int region) {
                eqDataBean.current = region;
                eqManager.saveEqModeData(eqMode.getId(), eqDataBean);
                updateCurrentFreqEq();
                EffectManager.getInstance().setEq();
            }
        });
        // 恢复上次的所有频率、增益 、当前调节的区间
        updateAllFreqEq();
    }

    private void initAdjustBtn() {
        btnGain = getView(R.id.btn_group_eq);
        btnFreq = getView(R.id.btn_group1);
        btnEqValue = getView(R.id.btn_group2);
        btnFreq.setTitle(ResUtils.getString(R.string.frequency));
        btnEqValue.setTitle(ResUtils.getString(R.string.q_value_adjust));
        // 恢复最后调节的一段频率
        updateCurrentFreqEq();
        btnGain.setListener(adjustListener);
        btnFreq.setListener(adjustListener);
        btnEqValue.setListener(adjustListener);
    }


    private CommAdjustButton.AdjustListener adjustListener = new CommAdjustButton.AdjustListener() {
        @Override
        public void onAdjust(View btn, boolean up) {
            if (btn.equals(btnGain)) {
                // 调节增益
                int gain = EqRegionDataLogic.getGain(eqSquareBars.getProgress(), up);
                eqSquareBars.updateProgress(gain);
                btnGain.setValue(gain + ResUtils.getString(R.string.gain_unit));
                eqDataBean.gains[eqSquareBars.getRegion()] = gain;
            } else if (btn.equals(btnFreq)) {
                // 调节频率
                int freq = EqRegionDataLogic.getFreq(eqSquareBars.getRegion(),
                        eqSquareBars.getCurrentTitle(), eqSquareBars.getPrevTitle(), eqSquareBars.getNextTitle(), up);
                eqSquareBars.updateTitle(freq);
                btnFreq.setValue(EqUtils.getFreq2Str(freq));
                eqDataBean.frequencies[eqSquareBars.getRegion()] = freq;
            } else {
                // 调节q值
                double eqValue = EqRegionDataLogic.getEqValue(eqDataBean.qValues[eqSquareBars.getRegion()], up);
                eqDataBean.qValues[eqSquareBars.getRegion()] = eqValue;
                btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqValue)]);
            }
            eqManager.saveEqModeData(eqMode.getId(), eqDataBean);
            EffectManager.getInstance().setEq();
        }
    };

    private void updateAllFreqEq() {
        eqSquareBars.updateProgesses(eqDataBean.gains);
        eqSquareBars.updateTitles(eqDataBean.frequencies);
        eqSquareBars.updateRegion(eqDataBean.current);
    }

    private void updateCurrentFreqEq() {
        btnGain.setValue(eqDataBean.gains[eqDataBean.current] + ResUtils.getString(R.string.gain_unit));
        btnFreq.setValue(EqUtils.getFreq2Str(eqDataBean.frequencies[eqDataBean.current]));
        btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqDataBean.qValues[eqDataBean.current])]);
    }

    private void test() {
        if (eqDataDetailDialog == null) {
            eqDataDetailDialog = new EqDataDetailDialog(context());
        }
        TestUtils.test(titleBar, eqDataDetailDialog, eqDataBean);
    }

}
