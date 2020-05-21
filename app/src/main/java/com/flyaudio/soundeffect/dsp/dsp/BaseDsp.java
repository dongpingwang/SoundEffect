package com.flyaudio.soundeffect.dsp.dsp;

import com.flyaudio.dsp.DspManager;
import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.config.AppPreferences;

/**
 * @author Dongping Wang
 * date 2020/5/1  17:51
 * email wangdongping@flyaudio.cn
 */
public class BaseDsp implements IDsp {

    private static final boolean DEBUG = AppPreferences.LOGGABLE_DSP;
    private static volatile DspManager dspManager;

    public static BaseDsp getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static BaseDsp instance = new BaseDsp();
    }

    private BaseDsp() {
        getDspManager();
    }

    private DspManager getDspManager() {
        if (dspManager == null) {
            dspManager = DspManager.getInstance();
        }
        return dspManager;
    }


    @Override
    public int setEq(int channel, int region, double freq, double q, double gain) {
        int result = getDspManager().setEQ(channel, region, freq, q, gain);
        if (DEBUG) {
            Logger.i("setEQ: channel = " + channel + " region = " + region + "  freq = " + freq + " q = " + q + " gain = " + gain + " | result = " + result);
        }
        return result;
    }


    @Override
    public int setEqFilter(int channel, int type, double freq, double q, double gain, boolean enable) {
        int result = getDspManager().setPEQFilter(channel, type, freq, q, gain, enable);
        if (DEBUG) {
            Logger.i("setPEQFilter: channel = " + channel + " type = " + type + "  freq = " + freq + " q = " + q + " gain = " + gain + " enable = " + enable + " | result = " + result);
        }
        return result;
    }

    @Override
    public int setChannel(int channel, boolean enable) {
        int result = getDspManager().setChannel(channel, enable);
        if (DEBUG) {
            Logger.i("setChannel: channel = " + channel + " enable = " + enable + " | result = " + result);
        }
        return result;
    }

    @Override
    public int setDelay(int channel, int delayValue) {
        int result = getDspManager().setDelay(channel, delayValue);
        if (DEBUG) {
            Logger.i("setDelay: channel = " + channel + " delayValue = " + delayValue + " | result = " + result);
        }
        return result;
    }


    @Override
    public int setBalance(int channel, double balanceValue) {
        int result = getDspManager().setBalance(channel, balanceValue);
        if (DEBUG) {
            Logger.i("setBalance: channel = " + channel + " balanceValue = " + balanceValue + " | result = " + result);
        }
        return result;

    }

    @Override
    public int setDspPhaseSwitch(int channel, boolean reverse) {
        int result = getDspManager().setDspPhaseSwitch(channel, reverse);
        if (DEBUG) {
            Logger.i("setDspPhaseSwitch: channel = " + channel + " reverse = " + reverse + " | result = " + result);
        }
        return result;
    }


}
