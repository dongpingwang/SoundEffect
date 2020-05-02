package com.flyaudio.soundeffect.delay.logic;

/**
 * @author Dongping Wang
 * date 2020/5/1  22:42
 * email wangdongping@flyaudio.cn
 */
public final class DelayManager {

    private DelayManager() {

    }

    private static class InstanceHolder {
        private static DelayManager instance = new DelayManager();
    }

    public static DelayManager getInstance() {
        return InstanceHolder.instance;
    }
}
