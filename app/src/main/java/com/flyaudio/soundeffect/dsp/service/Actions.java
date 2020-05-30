package com.flyaudio.soundeffect.dsp.service;

import com.flyaudio.lib.utils.AppUtils;

/**
 * @author Dongping Wang
 * date 2020/5/30  17:09
 * email wangdongping@flyaudio.cn
 */
final class Actions {

    static final String EFFECT_ACTION = AppUtils.getPackageName() + ".effect";
    static final String EFFECT_EXTRA = "effectType";

    /**
     * 开机初始化音效设置
     */
    static final int EXTRA_INIT = 0;

    /**
     * 设置一段EQ
     */
    static final int EXTRA_SET_EQ = 1;

    /**
     * 设置13段EQ
     */
    static final int EXTRA_SET_ALL_EQ = 2;

    /**
     * 切换收听位置
     */
    static final int EXTRA_SET_LISTEN_POSITION = 3;
    /**
     * 设置某个喇叭的通道延时
     */
    static final int EXTRA_SET_DELAY = 4;
    static final String EXTRA_SPEAKER = "speaker";
    static final String EXTRA_DELAY_VALUE = "delay_value";

    /**
     * 设置某个喇叭的音量值
     */
    static final int EXTRA_SET_BALANCE = 5;
    static final String EXTRA_BALANCE_VALUE = "balance_value";
    /**
     * 设置重低音喇叭是否打开输出
     */
    static final int EXTRA_SET_TRUMPET_SUBWOOFER = 6;
    /**
     * 设置重低音喇叭是否正反相位
     */
    static final int EXTRA_SET_TRUMPET_SUBWOOFER_REVERSE = 7;
    /**
     * 设置后排喇叭是否打开输出
     */
    static final int EXTRA_SET_TRUMPET_BACK_ROW = 8;

    static final int EXTRA_SET_EQ_FILTER = 9;

}
