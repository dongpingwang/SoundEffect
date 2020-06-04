package com.flyaudio.soundeffect.dsp.dsp.check;

import com.flyaudio.dsp.DspManager;
import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.config.AppPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-6-3
 * email wangdongping@flyaudio.cn
 */
public class BaseCheck implements IDspCheck, DspManager.ServiceConnection {

    private static final boolean DEBUG = AppPreferences.LOGGABLE_DSP;
    private static volatile DspManager dspManager;
    private List<DspServiceConnection> connections = new ArrayList<>();


    private BaseCheck() {
        getDspManager();
    }

    private static class InstanceHolder {
        private static BaseCheck instance = new BaseCheck();
    }

    public static BaseCheck getInstance() {
        return InstanceHolder.instance;
    }

    private synchronized DspManager getDspManager() {
        if (dspManager == null) {
            dspManager = DspManager.getInstance();
        }
        return dspManager;
    }


    @Override
    public int getDspInitState() {
        int dspInitState = getDspManager().getDspInitState();
        if (DEBUG) {
            Logger.d("getDspInitState: | dspInitState = " + dspInitState);
        }
        return dspInitState;
    }


    @Override
    public void setAutoConnect(boolean autoConnect) {
        getDspManager().setAutoConnect(autoConnect);
        if (DEBUG) {
            Logger.d("setAutoConnect: " + autoConnect);
        }
    }

    @Override
    public void registerServiceConnection(DspServiceConnection connection) {
        if (DEBUG) {
            Logger.d("registerServiceConnection:");
        }
        connections.add(connection);
        getDspManager().registerServiceConnection(this);
    }

    @Override
    public void unregisterServiceConnection(DspServiceConnection connection) {
        if (DEBUG) {
            Logger.d("unregisterServiceConnection:");
        }
        connections.remove(connection);
        getDspManager().unregisterServiceConnection(this);
    }

    @Override
    public void onServiceConnected() {
        if (DEBUG) {
            Logger.d("onServiceConnected:");
        }
        for (DspServiceConnection conn : connections) {
            conn.onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        if (DEBUG) {
            Logger.d("onServiceDisconnected:");
        }
        for (DspServiceConnection conn : connections) {
            conn.onServiceDisconnected();
        }
    }

}
