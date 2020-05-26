package com.flyaudio.soundeffect.filter.logic;

import android.support.annotation.IntDef;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;
import com.flyaudio.soundeffect.dsp.logic.DspHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import static com.flyaudio.soundeffect.filter.logic.EqFilterManager.FilterChannel.*;

/**
 * @author Dongping Wang
 * @date 20-5-23
 * email wangdongping@flyaudio.cn
 */
public final class EqFilterManager {

    /**
     * 是否打开高低通滤波
     */
    private static final String KEY_EQ_FILTER_ENABLE = "eq_filter_enable_%d";
    /**
     * 高低通滤波频率
     */
    private static final String KEY_EQ_FILTER_FREQ = "eq_filter_freq_%d";
    /**
     * 高低通滤波斜率
     */
    private static final String KEY_EQ_FILTER_SLOPE = "eq_filter_slope_%d";

    /**
     * 当前调节的那个滤波
     */
    private static final String KEY_EQ_FILTER_CURRENT = "eq_filter_current";


    @IntDef({FRONT_ROW, REAR_ROW, SUBWOOFER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FilterChannel {
        /**
         * 前排HPF滤波
         */
        int FRONT_ROW = 0;
        /**
         * 后排HPF滤波
         */
        int REAR_ROW = 1;
        /**
         * 重低音LPF滤波
         */
        int SUBWOOFER = 2;
    }


    public static final int[] FILTER_CHANNELS = {FRONT_ROW, REAR_ROW, SUBWOOFER};
    // 对应底层参数
    private static final SparseIntArray FILTER_TYPE_MAP = new SparseIntArray() {{
        put(FRONT_ROW, DspConstants.EqFilterType.GEQ_HPF.getValue());
        put(REAR_ROW, DspConstants.EqFilterType.GEQ_HPF.getValue());
        put(SUBWOOFER, DspConstants.EqFilterType.GEQ_LPF.getValue());

    }};
    // 增益
    private static final int EQ_FILTER_GAIN = 0;

    private static SparseArray<Double> slopeEqValueMap;

    private EqFilterManager() {
        // singleton
    }

    private static final class InstanceHolder {
        private static EqFilterManager instance = new EqFilterManager();
    }

    public static EqFilterManager getInstance() {
        return InstanceHolder.instance;
    }


    synchronized public SparseArray<Double> getSlopeEqValueMap() {
        if (slopeEqValueMap == null) {
            slopeEqValueMap = EqFilterDataLogic.getSlopEqValue();
        }
        return slopeEqValueMap;
    }

    /**
     * 高低通滤波是否能用
     *
     * @param type 滤波类型
     * @return 相应滤波是否能用，默认关闭
     */
    public boolean isFilterEnable(@FilterChannel int type) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FILTER_ENABLE, type);
        return SPCacheHelper.getInstance().getBoolean(spKey, false);
    }

    /**
     * 保存高低通滤波开关状态
     *
     * @param type   滤波类型
     * @param enable 是否能用
     */
    public void saveFilterEnable(@FilterChannel int type, boolean enable) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FILTER_ENABLE, type);
        SPCacheHelper.getInstance().put(spKey, enable);
    }

    /**
     * 获取高低通滤波的频率
     *
     * @param channel 滤波类型
     * @return 相应滤波的当前频率
     */
    public int getFilterFreq(@FilterChannel int channel) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FILTER_FREQ, channel);
        return SPCacheHelper.getInstance().getInt(spKey, EqFilterDataLogic.HPF[0]);
    }

    /**
     * 保存高低通滤波频率
     *
     * @param channel 滤波类型
     * @param freq    频率
     */
    public void saveFilterFreq(@FilterChannel int channel, int freq) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FILTER_FREQ, channel);
        SPCacheHelper.getInstance().put(spKey, freq);
    }

    /**
     * 获取高低通滤波的斜率
     *
     * @param channel 滤波类型
     * @return 相应滤波的当前斜率
     */
    public int getFilterSlope(@FilterChannel int channel) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FILTER_SLOPE, channel);
        return SPCacheHelper.getInstance().getInt(spKey, EqFilterDataLogic.SLOPES[1]);
    }

    /**
     * 保存高低通滤波斜率
     *
     * @param channel 滤波类型
     * @param slope   斜率
     */
    public void saveFilterSlope(@FilterChannel int channel, int slope) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_FILTER_SLOPE, channel);
        SPCacheHelper.getInstance().put(spKey, slope);
    }

    /**
     * 获取当前调节的那个滤波
     */
    public int getCurrentFilter() {
        return SPCacheHelper.getInstance().getInt(KEY_EQ_FILTER_CURRENT, FRONT_ROW);
    }

    /**
     * 保存当前调节的那个滤波
     *
     * @param channel 滤波通道
     */
    public void saveCurrentFilter(@FilterChannel int channel) {
        SPCacheHelper.getInstance().put(KEY_EQ_FILTER_CURRENT, channel);
    }


    public void setEqFilter(@FilterChannel int channel, double freq, int slope) {
        DspHelper.getDspHelper().setEqFilter(channel + 1, FILTER_TYPE_MAP.get(channel), freq, getSlopeEqValueMap().get(slope), EQ_FILTER_GAIN, isFilterEnable(channel));
    }
}
