package com.flyaudio.soundeffect.trumpet.logic;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:44
 * email wangdongping@flyaudio.cn
 */
public final class TrumpetManager {
    private TrumpetManager() {

    }

    private static class InstanceHolder {
        private static TrumpetManager instance = new TrumpetManager();
    }

    public static TrumpetManager getInstance() {
        return InstanceHolder.instance;
    }
}
