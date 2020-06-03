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
     * 一个eq模式的所有频率、增益、q值 、当前调节的一段频率索引
     */
    protected static final String KEY_EQ_MODE = "eq_mode_%d";

    /**
     * 保存一个eq模式的所有频率、增益、q值、当前调节的一段频率索引
     */
    public void saveEqModeData(int id, EqDataBean data) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE, id);
        SPCacheHelper.getInstance().put(spKey, data.toString());
    }

    /**
     * 获取一个eq模式的所有频率、增益、q值、当前调节的一段频率索引
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
            data = getDefaultEqModeData(id);
        }
        return data;
    }

    /**
     * 获取一个eq模式默认的所有频率、增益、q值、当前调节的一段频率索引
     */
    public EqDataBean getDefaultEqModeData(int id) {
        EqDataBean data = new EqDataBean();
        data.frequencies = EffectCommUtils.getFrequencies();
        data.gains = getDefaultGains(id);
        data.qValues = getDefaultEqValues();
        data.current = 0;
        return data;
    }

    /**
     * 获取默认的增益
     */
    private int[] getDefaultGains(int id) {
        if (id < 0 || id > EffectCommUtils.getEqGains().length - 1) {
            return new int[13];
        }
        int index = id < EQ_PRESET_COUNT ? id : EffectCommUtils.getEqGains().length - 1;
        int[] eqGain = EffectCommUtils.getEqGains()[index].clone();
        return eqGain;
    }

    /**
     * 获取默认的q值
     */
    private double[] getDefaultEqValues() {
        double[] values = new double[EffectCommUtils.getFrequencies().length];
        for (int i = 0; i < values.length; i++) {
            values[i] = EffectCommUtils.Q_VALUES[0];
        }
        return values;
    }

}
