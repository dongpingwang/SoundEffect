package com.flyaudio.soundeffect.trumpet.logic;

import com.flyaudio.lib.sp.SPCacheHelper;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:41
 * email wangdongping@flyaudio.cn
 */
public final class SubwooferManager {

    /**
     * 重低音输出是否打开
     */
    private final static String KEY_SUBOOFER_ON = "suboofer_on";


    private SubwooferManager() {

    }

    public static SubwooferManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static SubwooferManager instance = new SubwooferManager();
    }

    public boolean isSubwooferOn() {
        return SPCacheHelper.getInstance().getBoolean(KEY_SUBOOFER_ON, true);
    }

    public void saveSubwooferState(boolean isOn) {
        SPCacheHelper.getInstance().put(KEY_SUBOOFER_ON, isOn);
    }
}
