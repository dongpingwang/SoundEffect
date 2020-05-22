package com.flyaudio.soundeffect.trumpet.logic;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;
import com.flyaudio.soundeffect.dsp.logic.DspHelper;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:42
 * email wangdongping@flyaudio.cn
 */
public final class BackRowManager {

    /**
     * 后排输出是否打开
     */
    private final static String KEY_BACK_ROW_ON = "back_row_on";

    private BackRowManager() {

    }

    public static BackRowManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static BackRowManager instance = new BackRowManager();
    }

    public void init() {
        setBackRowEnable(isBackRowOn());
    }

    /**
     * 后排输出是否打开
     */
    public boolean isBackRowOn() {
        return SPCacheHelper.getInstance().getBoolean(KEY_BACK_ROW_ON, true);
    }

    /**
     * 保存后排输出开关状态
     *
     * @param isOn 是否打开
     */
    public void saveBackRowState(boolean isOn) {
        SPCacheHelper.getInstance().put(KEY_BACK_ROW_ON, isOn);
    }

    /**
     * 配置后排喇叭输出开关
     *
     * @param enable 是否打开后排输出
     */
    public void setBackRowEnable(boolean enable) {
        DspHelper.getDspHelper().setChannel(DspConstants.AudioSpeakerLayout.SPEAKER_LAYOUT_REAR.getValue(), enable);
        // TODO 这里要联动设置后排喇叭音量
    }

}
