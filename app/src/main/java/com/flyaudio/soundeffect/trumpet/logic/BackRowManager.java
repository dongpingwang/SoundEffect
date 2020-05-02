package com.flyaudio.soundeffect.trumpet.logic;

import com.flyaudio.lib.sp.SPCacheHelper;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:42
 * email wangdongping@flyaudio.cn
 */
public final class BackRowManager {

    /**
     * 后排输出是否打开
     */
    private final static String KEY_BACK_ROW_ON = "back_row_on";

    private BackRowManager() {

    }

    public static BackRowManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static BackRowManager instance = new BackRowManager();
    }

    public boolean isBackRowOn() {
        return SPCacheHelper.getInstance().getBoolean(KEY_BACK_ROW_ON, true);
    }

    public void saveBackRowState(boolean isOn) {
        SPCacheHelper.getInstance().put(KEY_BACK_ROW_ON, isOn);
    }

}
