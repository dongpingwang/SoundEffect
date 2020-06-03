package com.flyaudio.soundeffect.dsp.dsp.check;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.config.AppPreferences;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;

/**
 * @author Dongping Wang
 * @date 20-6-3
 * email wangdongping@flyaudio.cn
 */
public class EmptyCheck implements IDspCheck {

    private static final int FAIL_CODE = DspConstants.ResultCode.FAIL.getValue();
    private static final boolean DEBUG = AppPreferences.LOGGABLE_DSP;

    @Override
    public int getDspInitState() {
        if (DEBUG) {
            Logger.d("getDspInitState: | dspInitState = " + FAIL_CODE);
        }
        return FAIL_CODE;
    }

    @Override
    public void setAutoConnect(boolean autoConnect) {
        if (DEBUG) {
            Logger.d("setAutoConnect: " + autoConnect);
        }
    }

    @Override
    public void registerServiceConnection(DspServiceConnection connection) {
        if (DEBUG) {
            Logger.d("registerServiceConnection: " );
        }
    }
}
