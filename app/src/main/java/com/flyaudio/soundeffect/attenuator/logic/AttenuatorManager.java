package com.flyaudio.soundeffect.attenuator.logic;

import android.support.annotation.IntRange;
import android.support.annotation.Size;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.speaker.logic.VolumeManager;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Dongping Wang
 * @date 20-5-6
 * email wangdongping@flyaudio.cn
 */
public final class AttenuatorManager extends TouchValueLogic {

    private static final int BALANCE_MIN = -100;
    private static final int BALANCE_MAX = 0;

    /**
     * 保存衰减平衡的喇叭音量
     */
    private static final String KEY_BALANCE = "attenuator_balance_%d";


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
        Logger.d("坐标为:(%d, %d)", x, y);
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
        int[] values = {fl, fr, bl, br};
        setBalances(values);
        saveBalances(values);

    }

    public void setXBalanceByWeight(@IntRange(from = 0, to = 10) int x) {
        Logger.d("坐标为:" + x);
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
        setBalances(new int[]{fl, fr, bl, br});
        // 这里只保存前排的衰减平衡值到SP
        saveBalances(new int[]{fl, fr});

    }

    private void setBalances(@Size(4) int[] values) {
        Logger.d("setBalances: 前左/前右/后左/后右喇叭的衰减平衡音量值 = " + Arrays.toString(values));
        // 依次设置前左 前右 后左 后右喇叭的衰减平衡音量值
        for (int i = 0; i < values.length; i++) {
            EffectManager.getInstance().setBalance(Constants.SPEAKER_TYPES[i], values[i]);
            VolumeManager.getInstance().saveVolume(Constants.SPEAKER_TYPES[i], values[i]);
        }
    }

    private void saveBalances(int[] values) {
        for (int i = 0; i < values.length; i++) {
            saveBalance(Constants.SPEAKER_TYPES[i], values[i]);
        }
    }

    private void saveBalance(@Constants.ListenPositionSpeakerType int speaker, int value) {
        String spKey = String.format(Locale.getDefault(), KEY_BALANCE, speaker);
        SPCacheHelper.getInstance().putInt(spKey, value);
    }

    /**
     * 获取前左、前右、后左、后右喇叭对应的衰减平衡的值
     *
     * @param speaker 相应位置的喇叭
     * @return 相应喇叭的衰减平衡音量
     */
    private int getBalance(@Constants.ListenPositionSpeakerType int speaker) {
        String spKey = String.format(Locale.getDefault(), KEY_BALANCE, speaker);
        return SPCacheHelper.getInstance().getInt(spKey, 0);
    }
}
