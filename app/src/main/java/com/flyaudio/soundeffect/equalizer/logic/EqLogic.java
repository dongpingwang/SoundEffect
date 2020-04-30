package com.flyaudio.soundeffect.equalizer.logic;

import android.text.TextUtils;

import com.flyaudio.lib.json.handler.GsonHandler;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.comm.config.EffectConfigUtils;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.google.gson.Gson;

import java.util.Locale;

/**
 * @author Dongping Wang
 * @date 20-4-30
 * email wangdongping@flyaudio.cn
 */
public class EqLogic {

    protected static final int EQ_PRESET_COUNT = EffectConfigUtils.EQ_PRESET_COUNT;

    /**
     * 一个eq模式的所有频率、增益、q值
     */
    private static final String KEY_EQ_MODE = "eq_mode_%d";
    /**
     * 最后调节的一段eq索引
     */
    private static final String KEY_EQ_LAST_ADJUST = "eq_last_adjust_%d";


    public void saveEqModeData(int id, EqDataBean data) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE, id);
        GsonHandler handler = new GsonHandler(new Gson());
        String value = handler.toJson(data);
        SPCacheHelper.getInstance().put(spKey, value);
    }


    public EqDataBean getEqModeData(int id) {
        EqDataBean data;
        String spKey = String.format(Locale.getDefault(), KEY_EQ_MODE, id);
        GsonHandler handler = new GsonHandler(new Gson());

        String value = SPCacheHelper.getInstance().getString(spKey);
        if (!TextUtils.isEmpty(value)) {
            data = handler.fromJson(value, EqDataBean.class);
        } else {
            data = new EqDataBean();
            data.frequencies = EffectConfigUtils.getFrequencies();
            data.gains = getDefaultGains(id);
            data.qValues = getDefaultEqValues(id);
        }
        return data;
    }


    public int[] getDefaultGains(int id) {
        return EffectConfigUtils.getEqGains()[id < EQ_PRESET_COUNT ? id : EffectConfigUtils.getEqGains().length - 1];
    }

    public double[] getDefaultEqValues(int id) {
        double[] values = new double[EffectConfigUtils.getFrequencies().length];
        for (double value : values) {
            value = EffectConfigUtils.Q_VALUES[0];
        }
        return values;
    }


    public int getLastAdjustIndex(int id) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_LAST_ADJUST, id);
        return SPCacheHelper.getInstance().getInt(spKey);
    }

    public void saveLastAdjustIndex(int id, int index) {
        String spKey = String.format(Locale.getDefault(), KEY_EQ_LAST_ADJUST, id);
        SPCacheHelper.getInstance().put(spKey, index);
    }
}
