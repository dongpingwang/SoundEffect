package com.flyaudio.soundeffect.equalizer.bean;

import com.flyaudio.lib.json.handler.GsonHandler;
import com.google.gson.Gson;

/**
 * @author Dongping Wang
 * @date 20-4-30
 * email wangdongping@flyaudio.cn
 */
public class EqDataBean {

    public int[] frequencies;
    public int[] gains;
    public double[] qValues;

    @Override
    public String toString() {
        GsonHandler handler = new GsonHandler(new Gson());
        String value = handler.toJson(this);
        return value;
    }
}
