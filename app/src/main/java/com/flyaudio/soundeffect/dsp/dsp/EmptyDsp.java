package com.flyaudio.soundeffect.dsp.dsp;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.config.AppPreferences;

/**
 * @author Dongping Wang
 * date 2020/5/1  17:51
 * email wangdongping@flyaudio.cn
 */
public class EmptyDsp implements IDsp {

    private static final int FAIL_CODE = DspConstants.ResultCode.FAIL.getValue();
    private static final boolean DEBUG = AppPreferences.LOGGABLE_DSP;


    @Override
    public int setEq(int channel, int region, double freq, double q, double gain) {
        if (DEBUG) {
            Logger.i("setEQ: channel = " + channel + " region = " + region + "  freq = " + freq + " q = " + q + " gain = " + gain + " | result = " + -1);
        }
        return FAIL_CODE;
    }

    @Override
    public int setEqFilter(int channel, int type, double freq, double q, double gain, boolean enable) {
        if (DEBUG) {
            Logger.i("setPEQFilter: channel = " + channel + " type = " + type + "  freq = " + freq + " q = " + q + " gain = " + gain + " enable = " + enable + " | result = " + -1);
        }
        return FAIL_CODE;
    }

    @Override
    public int setChannel(int channel, boolean enable) {
        if (DEBUG) {
            Logger.i("setChannel: channel = " + channel + " enable = " + enable + " | result = " + -1);
        }
        return FAIL_CODE;
    }

    @Override
    public int setDelay(int channel, int delayValue) {
        if (DEBUG) {
            Logger.i("setDelay: channel = " + channel + " delayValue = " + delayValue + " | result = " + -1);
        }
        return FAIL_CODE;
    }

    @Override
    public int setBalance(int channel, double balanceValue) {
        if (DEBUG) {
            Logger.i("setBalance: channel = " + channel + " balanceValue = " + balanceValue + " | result = " + -1);
        }
        return FAIL_CODE;
    }

    @Override
    public int setDspPhaseSwitch(int channel, boolean reverse) {
        if (DEBUG) {
            Logger.i("setDspPhaseSwitch: channel = " + channel + " reverse = " + reverse + " | result = " + -1);
        }
        return FAIL_CODE;
    }

    @Override
    public int setChannelVolume(int channel, double volumeValue) {
        if (DEBUG) {
            Logger.i("setChannelVolume: channel = " + channel + " volumeValue = " + volumeValue + " | result = " + -1);
        }
        return FAIL_CODE;
    }
}
