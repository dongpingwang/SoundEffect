package com.flyaudio.soundeffect.equalizer.activity;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.dialog.ResetDialog;
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
    private ResetDialog resetDialog;

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
                if (resetDialog == null) {
                    resetDialog = new ResetDialog(context());
                }
                resetDialog.show();
                resetDialog.setListener(new ResetDialog.ResetListener() {
                    @Override
                    public void onReset() {
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
                eqManager.saveLastAdjustIndex(eqMode.getId(), region);
                btnFreq.setValue(EqUtils.getFreq2Str(eqSquareBars.getCurrentTitle()));
                btnGain.setValue(eqDataBean.gains[eqSquareBars.getRegion()] + ResUtils.getString(R.string.gain_unit));
                btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqDataBean.qValues[eqSquareBars.getRegion()])]);
            }
        });

        // 恢复所有频率、增益
        eqSquareBars.updateProgesses(eqDataBean.gains);
        eqSquareBars.updateTitles(eqDataBean.frequencies);
        // 恢复当前调节的区间
        eqSquareBars.updateRegion(eqManager.getLastAdjustIndex(eqMode.getId()));

    }

    private void initAdjustBtn() {
        btnGain = getView(R.id.btn_group_eq);
        btnFreq = getView(R.id.btn_group1);
        btnEqValue = getView(R.id.btn_group2);
        btnFreq.setTitle(ResUtils.getString(R.string.frequency));
        btnEqValue.setTitle(ResUtils.getString(R.string.q_value_adjust));

        // 恢复最后调节的一段频率
        int lastAdjustIndex = eqManager.getLastAdjustIndex(eqMode.getId());
        btnGain.setValue(eqDataBean.gains[lastAdjustIndex] + ResUtils.getString(R.string.gain_unit));
        btnFreq.setValue(EqUtils.getFreq2Str(eqDataBean.frequencies[lastAdjustIndex]));
        btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqDataBean.qValues[lastAdjustIndex])]);

        // 调节增益
        btnGain.setListener(new CommVerticalAdjustButton.AdjustListener() {
            @Override
            public void onAdjust(boolean up) {
                int gain = EqRegionDataLogic.getGain(eqSquareBars.getProgress(), up);
                eqSquareBars.updateProgress(gain);
                btnGain.setValue(gain + ResUtils.getString(R.string.gain_unit));
                eqDataBean.gains[eqSquareBars.getRegion()] = gain;
                eqManager.saveEqModeData(eqMode.getId(), eqDataBean);
                eqManager.saveGain(eqMode.getId(), eqSquareBars.getRegion(), eqSquareBars.getCurrentTitle(), gain);
            }
        });


        // 调节频率
        btnFreq.setListener(new CommAdjustButton.AdjustListener() {
            @Override
            public void onAdjust(boolean up) {
                int freq = EqRegionDataLogic.getFreq(eqSquareBars.getRegion(),
                        eqSquareBars.getCurrentTitle(), eqSquareBars.getPrevTitle(), eqSquareBars.getNextTitle(), up);
                // 这段频率对应的增益与q值
                int gain = eqManager.getGain(eqMode.getId(), eqSquareBars.getRegion(), freq);
                double eqValue = eqManager.getEqValue(eqMode.getId(), eqSquareBars.getRegion(), freq);

                eqSquareBars.updateTitle(freq);
                eqSquareBars.updateProgress(gain);
                btnFreq.setValue(EqUtils.getFreq2Str(freq));
                btnGain.setValue(gain + ResUtils.getString(R.string.gain_unit));
                btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqValue)]);
                eqDataBean.frequencies[eqSquareBars.getRegion()] = freq;
                eqManager.saveEqModeData(eqMode.getId(), eqDataBean);
            }
        });

        // 调节q值
        btnEqValue.setListener(new CommAdjustButton.AdjustListener() {
            @Override
            public void onAdjust(boolean up) {
                double eqValue = EqRegionDataLogic.getEqValue(eqDataBean.qValues[eqSquareBars.getRegion()], up);
                eqDataBean.qValues[eqSquareBars.getRegion()] = eqValue;
                btnEqValue.setValue(EQ_VALUE_DESCRIPTION[EqRegionDataLogic.getEqValueIndex(eqValue)]);
                eqManager.saveEqModeData(eqMode.getId(), eqDataBean);
                eqManager.saveEqValue(eqMode.getId(), eqSquareBars.getRegion(), eqSquareBars.getCurrentTitle(), eqValue);
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
