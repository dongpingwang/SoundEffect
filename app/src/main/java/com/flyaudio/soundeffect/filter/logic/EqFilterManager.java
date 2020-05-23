package com.flyaudio.soundeffect.filter.logic;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.flyaudio.soundeffect.filter.logic.EqFilterManager.ChannelType.*;

/**
 * @author Dongping Wang
 * @date 20-5-23
 * email wangdongping@flyaudio.cn
 */
public final class EqFilterManager {

    /**
     * 是否打开高低通滤波
     */
    private static final String KEY_EQ_FILTER_ENABLE = "key_eq_filter_enable_%d";
    /**
     * 高低通滤波频率
     */
    private static final String KEY_EQ_FILTER_FREQ = "key_eq_filter_freq_%d";
    /**
     * 高低通滤波斜率
     */
    private static final String KEY_EQ_FILTER_SLOPE = "key_eq_filter_slope_%d";

    /**
     * 当前调节的那个滤波
     */
    private static final String KEY_EQ_FILTER_CURRENT = "key_eq_filter_current";


    @IntDef({FRONT_ROW, REAR_ROW, SUBWOOFER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChannelType {
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


    private EqFilterManager() {
        // singleton
    }

    private static final class InstanceHolder {
        private static EqFilterManager instance = new EqFilterManager();
    }

    public static EqFilterManager getInstance() {
        return InstanceHolder.instance;
    }

}
