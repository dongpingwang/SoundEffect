package com.flyaudio.soundeffect.equalizer.logic;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyaudio.lib.json.handler.GsonHandler;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.config.EffectCommUtils;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.google.gson.Gson;

import java.util.Locale;

/**
 * @author Dongping Wang
 * @date 20-4-30
 * email wangdongping@flyaudio.cn
 */
public class EqLogic {

    protected static final int EQ_PRESET_COUNT = EffectCommUtils.EQ_PRESET_COUNT;

    /**
     * 一个eq模式的所有频率、增益、q值
     */
    private static final String KEY_EQ_MODE = "eq_mode_%d";
    /**
     * 最后调节的一段eq索引
     */
    private static final String KEY_EQ_LAST_ADJUST = "eq_last_adjust_%d";

    /**
     * 保存一个eq模式的所有频率、增益、q值
     */
    public void saveEqModeData(int id, EqDataBean data) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE, id);
        // GsonHandler handler = new GsonHandler(new Gson());
        // String value = handler.toJson(data);
        SPCacheHelper.getInstance().put(spKey, data.toString());
    }

    /**
     * 获取一个eq模式的所有频率、增益、q值
     */
    @NonNull
    public EqDataBean getEqModeData(int id) {
        EqDataBean data;
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE, id);
        GsonHandler handler = new GsonHandler(new Gson());

        String value = SPCacheHelper.getInstance().getString(spKey);
        if (!TextUtils.isEmpty(value)) {
            data = handler.fromJson(value, EqDataBean.class);
        } else {
            data = new EqDataBean();
            data.frequencies = EffectCommUtils.getFrequencies();
            data.gains = getDefaultGains(id);
            data.qValues = getDefaultEqValues();
        }
        return data;
    }


    /**
     * 获取默认的增益
     */
    public int[] getDefaultGains(int id) {
        return EffectCommUtils.getEqGains()[id < EQ_PRESET_COUNT ? id : EffectCommUtils.getEqGains().length - 1];
    }

    /**
     * 获取默认的q值
     */
    public double[] getDefaultEqValues() {
        double[] values = new double[EffectCommUtils.getFrequencies().length];
        for (int i = 0; i < values.length; i++) {
            values[i] = EffectCommUtils.Q_VALUES[0];
        }
        return values;
    }

    /**
     * 获取最后调节的一段频率索引
     */
    public int getLastAdjustIndex(int id) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_LAST_ADJUST, id);
        return SPCacheHelper.getInstance().getInt(spKey);
    }

    /**
     * 保存最后调节的一段频率索引
     */
    public void saveLastAdjustIndex(int id, int index) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_LAST_ADJUST, id);
        SPCacheHelper.getInstance().put(spKey, index);
    }
}
