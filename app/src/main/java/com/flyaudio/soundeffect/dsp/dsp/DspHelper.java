package com.flyaudio.soundeffect.dsp.dsp;

import com.flyaudio.soundeffect.config.AppConfigUtils;
import com.flyaudio.soundeffect.dsp.dsp.check.BaseCheck;
import com.flyaudio.soundeffect.dsp.dsp.check.EmptyCheck;
import com.flyaudio.soundeffect.dsp.dsp.check.IDspCheck;

/**
 * @author Dongping Wang
 * date 2020/5/10  15:04
 * email wangdongping@flyaudio.cn
 */
public final class DspHelper {

    private static volatile IDsp dsp;
    private static volatile IDspCheck dspCheck;

    private DspHelper() {

    }

    public synchronized static IDsp getDspHelper() {
        if (dsp == null) {
            synchronized (DspHelper.class) {
                if (dsp == null) {
                    if (AppConfigUtils.isDspOn()) {
                        dsp = BaseDsp.getInstance();
                    } else {
                        dsp = new EmptyDsp();
                    }
                }
            }
        }
        return dsp;
    }

    public synchronized static IDspCheck getDspCheckHelper() {
        if (dspCheck == null) {
            synchronized (DspHelper.class) {
                if (dspCheck == null) {
                    if (AppConfigUtils.isDspOn()) {
                        dspCheck = BaseCheck.getInstance();
                    } else {
                        dspCheck = new EmptyCheck();
                    }
                }
            }
        }
        return dspCheck;
    }
}
