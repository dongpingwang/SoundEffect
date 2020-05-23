package com.flyaudio.soundeffect.attenuator.logic;

import android.support.annotation.IntRange;

import com.flyaudio.lib.sp.SPCacheHelper;

/**
 * @author Dongping Wang
 * @date 20-5-23
 * email wangdongping@flyaudio.cn
 */
class TouchValueLogic {

    static final int CENTER_LIMIT = 5;
    /**
     * 保存View上的坐标x
     */
    private static final String KEY_TOUCH_VALUE_X = "attenuator_touch_value_x";
    /**
     * 保存View上的坐标y
     */
    private static final String KEY_TOUCH_VALUE_Y = "attenuator_touch_value_y";

    /**
     * 当后排关闭时只有前排时保存View上的坐标x
     */
    private static final String KEY_TOUCH_VALUE_X_NO_REAR = "attenuator_touch_value_x_no_rear";

    /**
     * 保存View上的坐标(x,y)
     */
    public void saveTouchValue(@IntRange(from = 1, to = 11) int x, @IntRange(from = 1, to = 11) int y) {
        SPCacheHelper.getInstance().put(KEY_TOUCH_VALUE_X, x);
        SPCacheHelper.getInstance().put(KEY_TOUCH_VALUE_Y, y);
    }

    /**
     * 获取View上的坐标(x,y)，默认(6,6)居中显示
     */
    public int[] getTouchValue() {
        int x = SPCacheHelper.getInstance().getInt(KEY_TOUCH_VALUE_X, CENTER_LIMIT + 1);
        int y = SPCacheHelper.getInstance().getInt(KEY_TOUCH_VALUE_Y, CENTER_LIMIT + 1);
        return new int[]{x, y};
    }

    /**
     * 当后排关闭时只有前排时获取View上的坐标x
     */
    public int getTouchValueWhenRearOff() {
        return SPCacheHelper.getInstance().getInt(KEY_TOUCH_VALUE_X_NO_REAR, CENTER_LIMIT + 1);
    }

    /**
     * 当后排关闭时只有前排时保存View上的坐标x
     */
    public void saveTouchValueWhenRearOff(@IntRange(from = 1, to = 11) int x) {
        SPCacheHelper.getInstance().put(KEY_TOUCH_VALUE_X_NO_REAR, x);
    }
}
