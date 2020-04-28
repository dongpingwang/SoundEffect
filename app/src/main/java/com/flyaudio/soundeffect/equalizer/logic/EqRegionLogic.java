package com.flyaudio.soundeffect.equalizer.logic;

import android.text.TextUtils;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.comm.config.ConfigUtils;
import com.flyaudio.soundeffect.comm.util.SpConvertUtils;

import java.util.List;
import java.util.Locale;

/**
 * @author Dongping Wang
 * @date 2020/3/721:59
 * email wangdongping@flyaudio.cn
 */
public final class EqRegionLogic {

    /**
     * 一个EQ模式，其中每一个区间，每段Freq ,对应的gain
     */
    private static final String KEY_EQ_REGION_FREQ_GAIN = "key_eq_region_type_freq_gain_%d_%d_%d";

    /**
     * 一个EQ模式，其中每一个区间，每段Freq ,对应的q
     */
    private static final String KEY_EQ_REGION_FREQ_Q = "key_eq_region_type_freq_q_%d_%d_%d";

    /**
     * 调节的EQ模式最后的Frequencies，将所有值以分号分割连接成字符串
     */
    private static final String KEY_EQ_FREQUENCIES = "key_eq_frequencies_%d";

    /**
     * 调节的EQ模式最后的gains
     */
    private static final String KEY_EQ_GAINS = "key_eq_gains_%d";

    /**
     * 调节的EQ模式最后的region/freq/gain/qIndex
     */
    private static final String KEY_EQ_LAST = "key_eq_last_%d";

    /**
     * 一个EQ  一个区间当前调节的freq
     */
    private static final String KEY_EQ_REGION_FREQ = "key_eq_freq_%d_%d";

    /**
     * 对应宽，中，窄
     */
    public static final double[] Q_VALUES = new double[]{0.9D, 2.1D, 4.5D};


    private static final int DEF_REGION = 0;
    private static final int DEF_FREQ = 50;
    /**
     * 默认Q值为宽
     */
    private static final int DEF_Q_VALUE_INDEX = 0;
    private static final int CHANNEL = 0;

    private EqRegionLogic() {

    }

    private static class InstanceHolder {
        private static EqRegionLogic instance = new EqRegionLogic();
    }

    public static EqRegionLogic getInstance() {
        return InstanceHolder.instance;
    }


    public void init() {
//        int type = EqLogic.getInstance().getCurrentEqualizer();
//        int[] freqs = getEqFrequencies(type);
//        if (freqs == null) {
//            freqs = BaseDiff.instance().bandFreqs();
//        }
//        for (int i = 0; i < freqs.length; i++) {
//            setEQ(i, freqs[i], Q_VALUES[getRegionFreqQValue(type, i, freqs[i])], getRegionFreqGain(type, i, freqs[i]));
//        }
    }

    /**
     * 获取EQ模式某个区间当前调节的freq
     *
     * @param type   模式id
     * @param region 区间
     */
    public int getEqFreq(int type, int region) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_REGION_FREQ, type, region);
        int value = SPCacheHelper.getInstance().getInt(spKey, -1);
        if (value <= 0) {
            return ConfigUtils.getFrequencies()[region];
        }
        return value;
    }

    /**
     * 保存一个EQ 区间当前调节的freq
     */
    public void saveEqFreq(int type, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_REGION_FREQ, type, region);
        SPCacheHelper.getInstance().put(spKey, freq);
    }

    /**
     * 调节的EQ模式最后的Frequencies
     */
    public int[] getEqFrequencies(int type) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FREQUENCIES, type);
        String value = SPCacheHelper.getInstance().getString(spKey);
        if (!TextUtils.isEmpty(value)) {
            return SpConvertUtils.string2array(value);
        } else {
            return ConfigUtils.getFrequencies();
        }
    }

    /**
     * 保存一个EQ模式最后显示Frequencies
     *
     * @param type
     * @param freq
     */
    public void saveEqFrequencies(int type, int[] freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FREQUENCIES, type);
        SPCacheHelper.getInstance().put(spKey, SpConvertUtils.array2String(freq));
    }

    public int[] getEqGains(int type) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_GAINS, type);
        String value = SPCacheHelper.getInstance().getString(spKey);
        if (!TextUtils.isEmpty(value)) {
            return SpConvertUtils.string2array(value);
        } else {
            return EqLogic.getInstance().getDefaultEqualizerGains(type);
        }
    }


    /**
     * 保存一个EQ模式最后显示Gains
     *
     * @param type
     * @param gains
     */
    public void saveEqGains(int type, int[] gains) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_GAINS, type);
        SPCacheHelper.getInstance().put(spKey, SpConvertUtils.array2String(gains));
    }

    /**
     * 保存最后调节的一段
     *
     * @param type
     * @param region
     * @param gain
     * @param qIndex
     */
    public void saveEqLast(int type, int region, int freq, int gain, int qIndex) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_LAST, type);
        String value = region + "," + freq + "," + gain + "," + qIndex;
        SPCacheHelper.getInstance().put(spKey, value);
    }


    public int[] getEqLast(int type) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_LAST, type);
        String value = SPCacheHelper.getInstance().getString(spKey);
        if (TextUtils.isEmpty(value)) {
            return new int[]{DEF_REGION, DEF_FREQ, getDefGain(type), DEF_Q_VALUE_INDEX};
        } else {
            return SpConvertUtils.string2array(value);
        }
    }

    public int getDefGain(int type) {
        return EqLogic.getInstance().getDefaultEqualizerGains(type)[0];
    }

    /**
     * 保存一个EQ模式，一个Freq区间，相应的freq，增益值
     *
     * @param type
     * @param region
     * @param freq
     * @param gain
     */
    public void saveRegionFreqGain(int type, int region, int freq, int gain) {
        String spKey = String.format(Locale.getDefault(), KEY_REGION_FREQ_GAIN, type, region, freq);
        presUtils.putValue(spKey, gain);
    }

    /**
     * 保存一个EQ模式，一个Freq区间，相应的freq，Q值
     * 注意保存的是数组{2.5D, 4.3D, 9.1D}中的索引
     *
     * @param type
     * @param region
     * @param freq
     * @param qIndex
     */
    public void saveRegionFreqQValue(int type, int region, int freq, int qIndex) {
        String spKey = String.format(Locale.getDefault(), KEY_REGION_FREQ_Q, type, region, freq);
        presUtils.putValue(spKey, qIndex);
    }

    public int getRegionFreqGain(int type, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_REGION_FREQ_GAIN, type, region, freq);
        int DEF_Q_VALUE_GAIN = EqLogic.getInstance().getDefaultEqualizerGains(type)[region];
        return (int) presUtils.getValue(spKey, DEF_Q_VALUE_GAIN);
    }

    public int getRegionFreqQValue(int type, int region, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_REGION_FREQ_Q, type, region, freq);
        return (int) presUtils.getValue(spKey, DEF_Q_VALUE_INDEX);
    }

    public void clear(int type) {
        // 清空当前的所有频率和增益、最后一段
        if (presUtils.contains(String.format(KEY_EQ_FREQUENCIES, type))) {
            presUtils.remove(String.format(KEY_EQ_FREQUENCIES, type));
        }
        if (presUtils.contains(String.format(KEY_EQ_GAINS, type))) {
            presUtils.remove(String.format(KEY_EQ_GAINS, type));
        }
        if (presUtils.contains(String.format(KEY_EQ_LAST, type))) {
            presUtils.remove(String.format(KEY_EQ_LAST, type));
        }
        // 清空每个区间中每一段频率
        int[] freqs = BaseDiff.instance().bandFreqs();
        for (int i = 0; i < freqs.length; i++) {
            List<Integer> freqsByRegion = EqRegionDataLogic.getFrequenciesByRegion(i);
            for (int j = 0; j < freqsByRegion.size(); j++) {
                String spKey = String.format(Locale.getDefault(), KEY_REGION_FREQ_Q, type, i, freqsByRegion.get(j));
                String spKey2 = String.format(Locale.getDefault(), KEY_REGION_FREQ_GAIN, type, i, freqsByRegion.get(j));
                String spKey3 = String.format(KEY_EQ_REGION_FREQ, type, i);
                if (presUtils.contains(spKey)) {
                    presUtils.remove(spKey);
                }
                if (presUtils.contains(spKey2)) {
                    presUtils.remove(spKey2);
                }
                if (presUtils.contains(spKey3)) {
                    presUtils.remove(spKey3);
                }
            }
        }
    }

    public void setEQ(int region, double freq, double q, double gain) {
        //DspHelper.create().setEQ(CHANNEL, region, freq, q, gain);
    }
}
