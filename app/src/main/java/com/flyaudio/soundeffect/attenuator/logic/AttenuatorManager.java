package com.flyaudio.soundeffect.attenuator.logic;

import android.support.annotation.IntRange;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.dsp.DspHelper;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Dongping Wang
 * @date 20-5-6
 * email wangdongping@flyaudio.cn
 */
public final class AttenuatorManager extends TouchValueLogic {

    public static final int BALANCE_MIN = -144;
    public static final int BALANCE_MAX = 0;

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


    public void init() {
        boolean backRowOn = BackRowManager.getInstance().isBackRowOn();
        for (int i = 0; i < Constants.SPEAKER_TYPES.length - 1; i++) {
            int balance = getBalance(Constants.SPEAKER_TYPES[i]);
            boolean backRow = Constants.SPEAKER_TYPES[i] == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_LEFT ||
                    Constants.SPEAKER_TYPES[i] == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_RIGHT;
            if (backRow && !backRowOn) {
                balance = BALANCE_MIN;
            }
            EffectManager.getInstance().setBalance(Constants.SPEAKER_TYPES[i], balance);
        }
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
        Logger.i("setXYBalanceByWeight: 坐标范围为0-10, x = " + x + " y = " + y + "衰减平衡最小值为-144");
        int fl, fr, bl, br;
        fl = fr = bl = br = 0;
        float n = 0F, m = 0F;
        if (x <= CENTER_LIMIT && y <= CENTER_LIMIT) {
            // 前左
            fl = 0;
            n = CENTER_LIMIT - x;
            m = CENTER_LIMIT - y;
            fr = Math.round(BALANCE_MIN * n / CENTER_LIMIT);
            bl = Math.round(BALANCE_MIN * m / CENTER_LIMIT);
            br = Math.round(BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT);
        }
        if (x >= CENTER_LIMIT && y <= CENTER_LIMIT) {
            // 前右
            n = x - CENTER_LIMIT;
            m = CENTER_LIMIT - y;
            fr = 0;
            fl = Math.round(BALANCE_MIN * n / CENTER_LIMIT);
            br = Math.round(BALANCE_MIN * m / CENTER_LIMIT);
            bl = Math.round(BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT);
        }
        if (x <= CENTER_LIMIT && y >= CENTER_LIMIT) {
            // 后左
            n = CENTER_LIMIT - x;
            m = y - CENTER_LIMIT;
            bl = 0;
            fl = Math.round(BALANCE_MIN * m / CENTER_LIMIT);
            br = Math.round(BALANCE_MIN * n / CENTER_LIMIT);
            fr = Math.round(BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT);
        }
        if (x >= CENTER_LIMIT && y >= CENTER_LIMIT) {
            // 后右
            n = x - CENTER_LIMIT;
            m = y - CENTER_LIMIT;
            br = 0;
            fr = Math.round(BALANCE_MIN * m / CENTER_LIMIT);
            bl = Math.round(BALANCE_MIN * n / CENTER_LIMIT);
            fl = Math.round(BALANCE_MIN * Math.max(n, m) / CENTER_LIMIT);
        }

        if (y == CENTER_LIMIT && x <= CENTER_LIMIT) {
            // x轴左部分
            n = CENTER_LIMIT - x;
            m = 0;
            fl = bl = 0;
            fr = br = Math.round(BALANCE_MIN * n / CENTER_LIMIT);
        }
        if (y == CENTER_LIMIT && x >= CENTER_LIMIT) {
            // x轴右部分
            n = x - CENTER_LIMIT;
            m = 0;
            fr = br = 0;
            fl = bl = Math.round(BALANCE_MIN * n / CENTER_LIMIT);
        }
        if (x == CENTER_LIMIT && y <= CENTER_LIMIT) {
            // y轴上部分
            n = 0;
            m = CENTER_LIMIT - y;
            fl = fr = 0;
            bl = br = Math.round(BALANCE_MIN * m / CENTER_LIMIT);
        }
        if (x == CENTER_LIMIT && y >= CENTER_LIMIT) {
            // y轴下部分
            n = 0;
            m = y - CENTER_LIMIT;
            bl = br = 0;
            fl = fr = Math.round(BALANCE_MIN * m / CENTER_LIMIT);
        }

        fl = fl > 0 ? 0 : fl < BALANCE_MIN ? BALANCE_MIN : fl;
        fr = fr > 0 ? 0 : fr < BALANCE_MIN ? BALANCE_MIN : fr;
        bl = bl > 0 ? 0 : bl < BALANCE_MIN ? BALANCE_MIN : bl;
        br = br > 0 ? 0 : br < BALANCE_MIN ? BALANCE_MIN : br;

        int[] values = new int[]{fl, fr, bl, br};
        Logger.d("setBalances: 前左/前右/后左/后右喇叭的衰减平衡音量值 = " + Arrays.toString(values));
        for (int i = 0; i < values.length; i++) {
            EffectManager.getInstance().setBalance(Constants.SPEAKER_TYPES[i], values[i]);
            saveBalance(Constants.SPEAKER_TYPES[i], values[i]);
        }

    }

    public void setXBalanceByWeight(@IntRange(from = 0, to = 10) int x) {
        Logger.i("setXBalanceByWeight: 坐标范围为0-10, x = " + x + "衰减平衡最小值为-144");
        int fl, fr, bl, br;
        fl = fr = 0;
        bl = br = BALANCE_MIN;
        if (x < CENTER_LIMIT) {
            // 前左
            fr = Math.round(BALANCE_MIN * (CENTER_LIMIT - x) * 1.0F / CENTER_LIMIT);
        } else {
            // 前右
            fl = Math.round(BALANCE_MIN * (x - CENTER_LIMIT) * 1.0F / CENTER_LIMIT);
        }

        fl = fl > 0 ? 0 : fl < BALANCE_MIN ? BALANCE_MIN : fl;
        fr = fr > 0 ? 0 : fr < BALANCE_MIN ? BALANCE_MIN : fr;
        int[] values = new int[]{fl, fr, bl, br};
        Logger.d("setBalances: 前左/前右/后左/后右喇叭的衰减平衡音量值 = " + Arrays.toString(values));
        for (int i = 0; i < values.length; i++) {
            EffectManager.getInstance().setBalance(Constants.SPEAKER_TYPES[i], values[i]);
            // 不保存后排数据
            boolean saveFrontRow = Constants.SPEAKER_TYPES[i] == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_FRONT_LEFT ||
                    Constants.SPEAKER_TYPES[i] == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_FRONT_RIGHT;
            if (saveFrontRow) {
                saveBalance(Constants.SPEAKER_TYPES[i], values[i]);
            }
        }
    }

    /**
     * 关闭后排是对应后左后右喇叭音量设置为最小值，再次打开后排时恢复
     *
     * @param enable 后排是否可用
     */
    public void setBalanceIfBackRowOff(boolean enable) {
        // 喇叭设置中打开/关闭后排输出时，不改变SP中的后排喇叭的衰减平衡数据
        int bl, br;
        if (enable) {
            bl = getBalance(Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_LEFT);
            br = getBalance(Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_RIGHT);

        } else {
            bl = br = BALANCE_MIN;
        }
        EffectManager.getInstance().setBalance(Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_LEFT, bl);
        EffectManager.getInstance().setBalance(Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_RIGHT, br);
    }

    /**
     * 获取前左、前右、后左、后右喇叭对应的衰减平衡的值
     *
     * @param speaker 相应位置的喇叭
     * @return 相应喇叭的衰减平衡音量
     */
    public int getBalance(@Constants.ListenPositionSpeakerType int speaker) {
        String spKey = String.format(Locale.getDefault(), KEY_BALANCE, speaker);
        return SPCacheHelper.getInstance().getInt(spKey, 0);
    }

    private void saveBalance(@Constants.ListenPositionSpeakerType int speaker, int value) {
        String spKey = String.format(Locale.getDefault(), KEY_BALANCE, speaker);
        SPCacheHelper.getInstance().putInt(spKey, value);
    }

    public void setBalance(int speaker, int volume) {
        if (speaker == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_SUBWOOFER) {
            Integer[] swChannels = Constants.SPEAKER_TYPE_MAP_SUBWOOFER.get(speaker);
            for (int sw : swChannels) {
                DspHelper.getDspHelper().setBalance(sw, volume);
            }
        } else {
            int channel = Constants.SPEAKER_TYPE_MAP.get(speaker);
            DspHelper.getDspHelper().setBalance(channel, volume);
        }
    }
}
