package com.flyaudio.soundeffect.equalizer.logic;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.comm.config.EffectConfigUtils;

import java.util.Locale;

/**
 * 注意010平台一个区间中所有的频率对应的增益、Q值是一样的
 * (原先区分的逻辑废弃 wdp 2020.05.05)
 *
 * @author Dongping Wang
 * @date 20-4-30
 * email wangdongping@flyaudio.cn
 */
@Deprecated
public class EqRegionLogic extends EqLogic {

    /**
     * 一个eq模式一个区间一段频率所对应的增益
     */
    private static final String KEY_EQ_MODE_GAIN = "eq_mode_gain_%d_%d_%d";
    /**
     * 一个eq模式一个区间一段频率所对应的Q值
     */
    private static final String KEY_EQ_MODE_Q_VALUE = "eq_mode_q_value_%d_%d_%d";


    public int getGain(int id, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_GAIN, id, region, freq);
        return SPCacheHelper.getInstance().getInt(spKey);
    }

    public void saveGain(int id, int region, int freq, int value) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_GAIN, id, region, freq);
        SPCacheHelper.getInstance().put(spKey, value);
    }


    public double getEqValue(int id, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_Q_VALUE, id, region, freq);
        return SPCacheHelper.getInstance().getFloat(spKey, (float) EffectConfigUtils.Q_VALUES[0]);
    }

    public void saveEqValue(int id, int region, int freq, double value) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_Q_VALUE, id, region, freq);
        SPCacheHelper.getInstance().put(spKey, (float) value);
    }

}
