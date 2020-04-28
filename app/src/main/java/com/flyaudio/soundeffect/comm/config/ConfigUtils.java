package com.flyaudio.soundeffect.comm.config;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * @date 20-4-28
 * email wangdongping@flyaudio.cn
 */
public final class ConfigUtils {

    private ConfigUtils() {

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

    public static int[][] getEqGains() {
        return EQ_GAINS_13;
    }
}
