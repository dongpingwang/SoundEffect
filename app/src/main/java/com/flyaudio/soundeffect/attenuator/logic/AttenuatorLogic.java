package com.flyaudio.soundeffect.attenuator.logic;

import android.support.annotation.IntDef;

import com.flyaudio.lib.sp.SPCacheHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.flyaudio.soundeffect.attenuator.logic.AttenuatorLogic.BalanceType.*;

/**
 * @author Dongping Wang
 * @date 20-5-6
 * email wangdongping@flyaudio.cn
 */
public final class AttenuatorLogic {


    @IntDef({
            BALANCE_FRONT_LEFT,
            BALANCE_FRONT_RIGHT,
            BALANCE_BACK_LEFT,
            BALANCE_BACK_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BalanceType {
        int BALANCE_FRONT_LEFT = 0;
        int BALANCE_FRONT_RIGHT = 1;
        int BALANCE_BACK_LEFT = 2;
        int BALANCE_BACK_RIGHT = 3;
    }


    /**
     * 保存View上的坐标x
     */
    private static final String KEY_TOUCH_VALUE_X = "attenuator_touch_value_x";
    /**
     * 保存View上的坐标y
     */
    private static final String KEY_TOUCH_VALUE_Y = "attenuator_touch_value_y";
    /**
     * 保存喇叭音量
     */
    private static final String KEY_BALANCE = "attenuator_balance_%d";

    private static final int VALUE_BALANCE = 0;

    private AttenuatorLogic() {

    }

    private static class InstanceHolder {
        private static AttenuatorLogic instance = new AttenuatorLogic();
    }

    /**
     * 保存View上的坐标(x,y)
     */
    public void saveTouchValue(int x, int y) {
        SPCacheHelper.getInstance().put(KEY_TOUCH_VALUE_X, x);
        SPCacheHelper.getInstance().put(KEY_TOUCH_VALUE_Y, y);
    }

    /**
     * 获取View上的坐标(x,y)
     */
    public int[] getTouchValue() {
        int x = SPCacheHelper.getInstance().getInt(KEY_TOUCH_VALUE_X);
        int y = SPCacheHelper.getInstance().getInt(KEY_TOUCH_VALUE_Y);
        return new int[]{x, y};
    }


}
