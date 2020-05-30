package com.flyaudio.soundeffect.dsp.dsp;

import com.flyaudio.soundeffect.config.AppConfigUtils;

/**
 * @author Dongping Wang
 * date 2020/5/10  15:04
 * email wangdongping@flyaudio.cn
 */
public final class DspHelper {

    private static volatile IDsp dsp;

    private DspHelper() {
        // singleton
    }

    public synchronized static IDsp getDspHelper() {
        if (DspHelper.dsp == null) {
            synchronized (DspHelper.class) {
                if (DspHelper.dsp == null) {
                    if (AppConfigUtils.isDspOn()) {
                        DspHelper.dsp = BaseDsp.getInstance();
                    } else {
                        DspHelper.dsp = new EmptyDsp();
                    }
                }
            }
        }
        return DspHelper.dsp;
    }
}
