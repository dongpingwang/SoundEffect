package com.flyaudio.soundeffect.trumpet.logic;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;
import com.flyaudio.soundeffect.dsp.logic.DspHelper;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:41
 * email wangdongping@flyaudio.cn
 */
public final class SubwooferManager {

    /**
     * 重低音输出是否打开
     */
    private final static String KEY_SUBOOFER_ON = "suboofer_on";

    /**
     * 重低音是否反向
     */
    private final static String KEY_SUBOOFER_REVERSE = "suboofer_reverse";


    private final static int SWL = DspConstants.Channel.SWL.getValue();
    private final static int SWR = DspConstants.Channel.SWR.getValue();

    private SubwooferManager() {

    }

    public static SubwooferManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static SubwooferManager instance = new SubwooferManager();
    }

    public void init() {
        setSubwooferEnable(isSubwooferOn());
        setSubooferReverse(isSubwooferReverse());
    }

    /**
     * 重低音输出是否打开
     */
    public boolean isSubwooferOn() {
        return SPCacheHelper.getInstance().getBoolean(KEY_SUBOOFER_ON, true);
    }

    /**
     * 保存重低音输出开关状态
     *
     * @param isOn 是否打开重低音
     */
    public void saveSubwooferState(boolean isOn) {
        SPCacheHelper.getInstance().put(KEY_SUBOOFER_ON, isOn);
    }

    /**
     * 配置重低音喇叭输出开关
     *
     * @param enable 是否打开重低音输出
     */
    public void setSubwooferEnable(boolean enable) {
        DspHelper.getDspHelper().setChannel(DspConstants.AudioSpeakerLayout.SPEAKER_LAYOUT_SUBWOOF.getValue(), enable);
    }

    /**
     * 重低音LPF滤波是否反向，默认正相
     */
    public boolean isSubwooferReverse() {
        return SPCacheHelper.getInstance().getBoolean(KEY_SUBOOFER_REVERSE, false);
    }

    /**
     * 保存重低音LPF滤波相位状态
     *
     * @param isReverse 是否反相
     */
    public void saveSubwooferReverse(boolean isReverse) {
        SPCacheHelper.getInstance().put(KEY_SUBOOFER_REVERSE, isReverse);
    }

    public void setSubooferReverse(boolean isSubwooferReverse) {
        DspHelper.getDspHelper().setDspPhaseSwitch(SWL, isSubwooferReverse);
        DspHelper.getDspHelper().setDspPhaseSwitch(SWR, isSubwooferReverse);
    }
}
