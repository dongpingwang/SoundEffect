package com.flyaudio.soundeffect.trumpet.logic;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:41
 * email wangdongping@flyaudio.cn
 */
public final class SubooferManager {

    private SubooferManager() {

    }

    public static SubooferManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static SubooferManager instance = new SubooferManager();
    }
}
