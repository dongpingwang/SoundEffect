package com.flyaudio.soundeffect.equalizer.logic;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.config.EffectCommUtils;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;
import com.flyaudio.soundeffect.dsp.logic.DspHelper;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;

import java.util.Locale;

/**
 * 注意：010平台一个区间中的所有频率的增益和Q值相同
 *
 * @author Dongping Wang
 * @date 20-4-30
 * email wangdongping@flyaudio.cn
 */

public class EqRegionLogic extends EqLogic {

    /**
     * 全局eq通道
     */
    private static final int EQ_CHANNEL_ALL = DspConstants.Channel.ALL.getValue();

    /**
     * 一个eq模式一个区间一段频率所对应的增益
     */
    @Deprecated
    private static final String KEY_EQ_MODE_GAIN = "eq_mode_gain_%d_%d_%d";
    /**
     * 一个eq模式一个区间一段频率所对应的Q值
     */
    @Deprecated
    private static final String KEY_EQ_MODE_Q_VALUE = "eq_mode_q_value_%d_%d_%d";


    /**
     * 设置当前eq模式的所有频段eq
     *
     * @param id eq模式id
     */
    public void init(int id) {
        EqDataBean eqModeData = getEqModeData(id);
        Logger.d("当前eq模式的数据：" + eqModeData.toString());
        for (int i = 0; i < eqModeData.frequencies.length; i++) {
            setEq(i, eqModeData.frequencies[i], eqModeData.qValues[i], eqModeData.gains[i]);
        }
    }


    /**
     * 获取一个eq模式中某个区间中某段频率对应的增益
     *
     * @param id     eq模式
     * @param region eq区间
     * @param freq   频率
     * @return 增益值
     */
    @Deprecated
    public int getGain(int id, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_GAIN, id, region, freq);
        return SPCacheHelper.getInstance().getInt(spKey);
    }

    /**
     * 保存一个eq模式中某个区间中某段频率对应的增益
     *
     * @param id     eq模式
     * @param region eq区间
     * @param freq   频率
     */
    @Deprecated
    public void saveGain(int id, int region, int freq, int value) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_GAIN, id, region, freq);
        SPCacheHelper.getInstance().put(spKey, value);
    }


    /**
     * 获取一个eq模式中某个区间中某段频率对应的Q值
     *
     * @param id     eq模式
     * @param region eq区间
     * @param freq   频率
     * @return Q值
     */
    @Deprecated
    public double getEqValue(int id, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_Q_VALUE, id, region, freq);
        return SPCacheHelper.getInstance().getFloat(spKey, (float) EffectCommUtils.Q_VALUES[0]);
    }

    /**
     * 保存一个eq模式中某个区间中某段频率对应的Q值
     *
     * @param id     eq模式
     * @param region eq区间
     * @param freq   频率
     */
    @Deprecated
    public void saveEqValue(int id, int region, int freq, double value) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE_Q_VALUE, id, region, freq);
        SPCacheHelper.getInstance().put(spKey, (float) value);
    }

    /**
     * 设置eq
     *
     * @param region eq区间
     * @param freq   频率
     * @param q      Q值
     * @param gain   增益
     */
    public void setEq(int region, double freq, double q, double gain) {
        DspHelper.getDspHelper().setEq(EQ_CHANNEL_ALL, region, freq, q, gain);
    }
}
