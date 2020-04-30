package com.flyaudio.soundeffect.comm.config;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * @date 20-4-28
 * email wangdongping@flyaudio.cn
 */
public final class EffectConfigUtils {
    /**
     * 预置的eq模式数量
     */
    public static final int EQ_PRESET_COUNT = 5;
    /**
     * eq Q值数组 对应宽，中，窄
     */
    public static final double[] Q_VALUES = {0.9D, 2.1D, 4.5D};

    private EffectConfigUtils() {

    }

    /**
     * 获取频段(010平台31段eq分成了13个区间,这里是获取默认的13段频率)
     */
    public static int[] getFrequencies() {
        return ResUtils.getIntArray(R.array.eq_frequencies_13);
    }

    /**
     * 13EQ均衡器各通道的默认增益值
     */
    private static final int[][] EQ_GAINS_13 = {
            ResUtils.getIntArray(R.array.EqualizerFlat_13),
            ResUtils.getIntArray(R.array.EqualizerStrong_13),
            ResUtils.getIntArray(R.array.EqualizerVocalMusic_13),
            ResUtils.getIntArray(R.array.EqualizerNature_13),
            ResUtils.getIntArray(R.array.EqualizerSuperHeavy_13),
            ResUtils.getIntArray(R.array.EqualizerCustom_13),
            ResUtils.getIntArray(R.array.EqualizerCustom_13),
            ResUtils.getIntArray(R.array.EqualizerCustom_13),
    };

    /**
     * 获取默认EQ模式
     */
    public static String[] getEqNames() {
        return ResUtils.getStringArray(R.array.eq_names);
    }

    /**
     * 13EQ均衡器各通道的默认增益值
     */
    public static int[][] getEqGains() {
        return EQ_GAINS_13;
    }

}
