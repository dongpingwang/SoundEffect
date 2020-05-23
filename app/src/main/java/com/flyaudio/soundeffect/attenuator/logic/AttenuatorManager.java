package com.flyaudio.soundeffect.attenuator.logic;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.logic.DspHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import static com.flyaudio.soundeffect.attenuator.logic.AttenuatorManager.BalanceType.*;

/**
 * @author Dongping Wang
 * @date 20-5-6
 * email wangdongping@flyaudio.cn
 */
public final class AttenuatorManager extends TouchValueLogic {

    private static final int BALANCE_MIN = -100;
    private static final int BALANCE_MAX = 0;

    @IntDef({
            BALANCE_FRONT_LEFT,
            BALANCE_FRONT_RIGHT,
            BALANCE_BACK_LEFT,
            BALANCE_BACK_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BalanceType {
        /**
         * 前左喇叭
         */
        int BALANCE_FRONT_LEFT = 0;
        /**
         * 前右喇叭
         */
        int BALANCE_FRONT_RIGHT = 1;
        /**
         * 后左喇叭
         */
        int BALANCE_BACK_LEFT = 2;
        /**
         * 后右喇叭
         */
        int BALANCE_BACK_RIGHT = 3;
    }


    /**
     * 保存喇叭音量
     */
    private static final String KEY_BALANCE = "attenuator_balance_%d";

    private static final int VALUE_BALANCE = 0;

    private AttenuatorManager() {

    }

    private static class InstanceHolder {
        private static AttenuatorManager instance = new AttenuatorManager();
    }


    public static AttenuatorManager getInstance() {
        return InstanceHolder.instance;
    }


    /**
     * x轴(左右)表示:平衡 y轴(上下):衰减 对应方向的平衡与衰减叠加即为该方向的喇叭值
     * 对应view上的坐标
     * <p>
     * 这里以中心为参照，上下 +x 左右 +y
     *
     * @param x 0-10
     * @param y 0-10
     */
    public void setBalanceXYByWeight(@IntRange(from = 0, to = 10) int x, @IntRange(from = 0, to = 10) int y) {

        int fl, fr, bl, br;
        fl = fr = bl = br = 0;
        int n = 0, m = 0;
        if (x < CENTER_LIMIT && y < CENTER_LIMIT) {
            // 前左
            fl = 0;
            n = CENTER_LIMIT - x;
            m = CENTER_LIMIT - y;
            fr = BALANCE_MIN * n / CENTER_LIMIT;
            bl = BALANCE_MIN * m / CENTER_LIMIT;
            br = BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT;
        } else if (x > CENTER_LIMIT && y < CENTER_LIMIT) {
            // 前右
            n = x - CENTER_LIMIT;
            m = CENTER_LIMIT - y;
            fr = 0;
            fl = BALANCE_MIN * n / CENTER_LIMIT;
            br = BALANCE_MIN * m / CENTER_LIMIT;
            bl = BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT;
        } else if (x < CENTER_LIMIT && y > CENTER_LIMIT) {
            // 后左
            n = CENTER_LIMIT - x;
            m = y - CENTER_LIMIT;
            bl = 0;
            fl = BALANCE_MIN * m / CENTER_LIMIT;
            br = BALANCE_MIN * n / CENTER_LIMIT;
            fr = BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT;
        } else if (x > CENTER_LIMIT && y > CENTER_LIMIT) {
            // 后右
            n = x - CENTER_LIMIT;
            m = y - CENTER_LIMIT;
            br = 0;
            fr = BALANCE_MIN * m / CENTER_LIMIT;
            bl = BALANCE_MIN * n / CENTER_LIMIT;
            fl = BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT;
        }

        if (y == CENTER_LIMIT && x < CENTER_LIMIT) {
            // x轴左部分
            n = CENTER_LIMIT - x;
            m = 0;
            fl = bl = 0;
            fr = br = BALANCE_MIN * n / CENTER_LIMIT;
        } else if (y == CENTER_LIMIT && x > CENTER_LIMIT) {
            //  x轴右部分
            n = x - CENTER_LIMIT;
            m = 0;
            fr = br = 0;
            fl = bl = BALANCE_MIN * n / CENTER_LIMIT;
        } else if (x == CENTER_LIMIT && y < CENTER_LIMIT) {
            // y轴上部分
            n = 0;
            m = CENTER_LIMIT - y;
            fl = fr = 0;
            bl = br = BALANCE_MIN * m / CENTER_LIMIT;
        } else if (x == CENTER_LIMIT && y > CENTER_LIMIT) {
            // y轴下部分
            n = 0;
            m = y - CENTER_LIMIT;
            bl = br = 0;
            fl = fr = BALANCE_MIN * m / CENTER_LIMIT;
        } else if (x == CENTER_LIMIT && y == CENTER_LIMIT) {
            // 原点
            fl = fr = bl = fr = 0;
        }
        setBalances(fl, fr, bl, br);
    }

    public void setXBalanceByWeight(@IntRange(from = 0, to = 10) int x) {
        int fl, fr, bl, br;
        fl = fr = 0;
        bl = br = BALANCE_MIN;
        if (x < CENTER_LIMIT) {
            // 前左
            fr = BALANCE_MIN * (CENTER_LIMIT - x) / CENTER_LIMIT;
        } else {
            // 前右
            fl = BALANCE_MIN * (x - CENTER_LIMIT) / CENTER_LIMIT;
        }
        setBalances(fl, fr, bl, br);
    }

    private void setBalances(int fl, int fr, int bl, int br) {
        Logger.d("setBalances: fl = " + fl + "  fr = " + fr + "  bl = " + bl + "  br = " + br);
        setBalance(BALANCE_FRONT_LEFT, fl);
        setBalance(BALANCE_FRONT_RIGHT, fr);
        setBalance(BALANCE_BACK_LEFT, bl);
        setBalance(BALANCE_BACK_RIGHT, br);
        saveBalance(BALANCE_FRONT_LEFT, fl);
        saveBalance(BALANCE_FRONT_RIGHT, fr);
        saveBalance(BALANCE_BACK_LEFT, bl);
        saveBalance(BALANCE_BACK_RIGHT, br);
    }

    private void setBalance(@BalanceType int type, int value) {
        DspHelper.getDspHelper().setBalance(type + 1, value);
    }

    private void saveBalance(@BalanceType int type, int value) {
        String spKey = String.format(Locale.getDefault(), KEY_BALANCE, type);
        SPCacheHelper.getInstance().putInt(spKey, value);
    }

    /**
     * 获取前左、前右、后左、后右喇叭对应的衰减平衡的值
     *
     * @param type 相应位置的喇叭
     * @return
     */
    private int getBalance(@BalanceType int type) {
        String spKey = String.format(Locale.getDefault(), KEY_BALANCE, type);
        return SPCacheHelper.getInstance().getInt(spKey, 0);
    }
}
