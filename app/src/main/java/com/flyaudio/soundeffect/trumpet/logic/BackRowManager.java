package com.flyaudio.soundeffect.trumpet.logic;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:42
 * email wangdongping@flyaudio.cn
 */
public final class BackRowManager {

    private BackRowManager() {

    }

    public static BackRowManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder{
        private static BackRowManager instance = new BackRowManager();
    }
}
