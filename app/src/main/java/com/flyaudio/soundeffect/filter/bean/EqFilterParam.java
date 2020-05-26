package com.flyaudio.soundeffect.filter.bean;


/**
 * @author Dongping Wang
 * @date 2020/5/26 20:22
 * email wangdongping@flyaudio.cn
 */
public class EqFilterParam {

    public int channel = -1;
    public int freq;
    public int slope;
    public boolean enable;

    @Override
    public String toString() {
        return "EqFilterParam{" +
                "channel = " + channel +
                ", freq = " + freq +
                ", slope = " + slope +
                ", enable = " + enable +
                '}';
    }
}

