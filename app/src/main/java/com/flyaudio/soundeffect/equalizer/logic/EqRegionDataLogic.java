package com.flyaudio.soundeffect.equalizer.logic;

import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.SparseArray;

import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.config.EffectConfigUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dongping Wang
 * @date 2020/3/714:53
 * email wangdongping@flyaudio.cn
 */
public final class EqRegionDataLogic {

    private static final int GAIN_MAX = 14;
    private static final int GAIN_MIN = -14;
    private static final int GAIN_STEP = 1;
    private static final double[] Q_VALUES = EffectConfigUtils.Q_VALUES;

    /**
     * 标志是否可以循环调节:增益不循环调节,频率和Q值可以循环调节
     */
    private static final boolean LOOP = true;
    private static final boolean LOOP_GAIN = false;

    private static SparseArray<EqRegion> maps;

    private EqRegionDataLogic() {

    }

    public static SparseArray<EqRegion> getEqRegions() {
        try {
            if (maps == null) {
                synchronized (EqRegionDataLogic.class) {
                    if (maps == null) {
                        maps = parseXml();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maps;
    }

    public static int getGain(int current, boolean up) {
        return up ? getGainUp(current) : getGainDown(current);
    }

    private static int getGainUp(int current) {
        int will = current + GAIN_STEP;
        if (will > GAIN_MAX) {
            will = LOOP_GAIN ? GAIN_MIN : current;
        }
        return will;
    }

    private static int getGainDown(int current) {
        int will = current - GAIN_STEP;
        if (will < GAIN_MIN) {
            will = LOOP_GAIN ? GAIN_MAX : current;
        }
        return will;
    }

    public static int getFreq(int region, int current, int prev, int next, boolean up) {
        return up ? getFreqUp(region, current, prev, next) : getFreqDown(region, current, prev, next);
    }

    private static int getFreqUp(int region, int current, int prev, int next) {
        int will = current;
        List<Integer> ins = getIns(region, prev, next);
        for (int i = 0; i < ins.size(); i++) {
            if (ins.get(i) > current) {
                will = ins.get(i);
                break;
            }
        }
        if (ins.size() >= 2 && will == current) {
            if (LOOP) {
                will = ins.get(0);
            }
        }
        return will;
    }

    private static int getFreqDown(int region, int current, int prev, int next) {
        int will = current;
        List<Integer> ins = getIns(region, prev, next);
        for (int i = ins.size() - 1; i >= 0; i--) {
            if (ins.get(i) < current) {
                will = ins.get(i);
                break;
            }
        }
        if (ins.size() >= 2 && will == current) {
            if (LOOP) {
                will = ins.get(ins.size() - 1);
            }
        }
        return will;
    }


    public static double getEqValue(double value, boolean up) {
        int will, current;
        current = getEqValueIndex(value);
        if (up) {
            will = current + 1;
            if (will > Q_VALUES.length - 1) {
                will = LOOP ? 0 : current;
            }
        } else {
            will = current - 1;
            if (will < 0) {
                will = LOOP ? Q_VALUES.length - 1 : current;
            }
        }
        return Q_VALUES[will];
    }

    public static int getEqValueIndex(double value) {
        int index = 0;
        for (int i = 0; i < Q_VALUES.length; i++) {
            if (Math.abs(value - Q_VALUES[i]) == 0) {
                index = i;
                break;
            }
        }
        return index;
    }


    /**
     * 获取当前区间中在前后频率范围内可选择的所有频率集合
     *
     * @param region 当前区间
     * @param prev   前一个区间的频率
     * @param next   前一个区间的频率
     */
    private static List<Integer> getIns(int region, int prev, int next) {
        List<Integer> ins = new ArrayList<>();
        List<Integer> frequencies = getFrequenciesByRegion(region);
        for (int item : frequencies) {
            if (item > prev && item < next) {
                ins.add(item);
            }
        }
        return ins;
    }

    /**
     * 获取对应区间的所有频率集合
     *
     * @param region eq区间，值为0-12
     */
    private static List<Integer> getFrequenciesByRegion(int region) {
        return getEqRegions().get(region).frequencies;
    }

    private static SparseArray<EqRegion> parseXml() {
        SparseArray<EqRegion> maps = new SparseArray<>();
        XmlResourceParser parser = AppUtils.getContext().getResources().getXml(R.xml.eq_freq_region);
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.next() == XmlResourceParser.START_TAG && TextUtils.equals(parser.getName(), "region")) {
                    EqRegion region = new EqRegion();
                    region.region = parser.getAttributeIntValue(null, "name", 0);
                    List<Integer> frequenciesCopy = new ArrayList<>();
                    while (parser.next() != XmlResourceParser.END_TAG || !TextUtils.equals(parser.getName(), "region")) {
                        if (parser.getEventType() == XmlResourceParser.START_TAG && TextUtils.equals(parser.getName(), "freq")) {
                            frequenciesCopy.add(parser.getAttributeIntValue(null, "value", 0));
                        }
                    }
                    region.frequencies = frequenciesCopy;
                    maps.put(region.region, region);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }

    private static class EqRegion {
        private int region;
        private List<Integer> frequencies;
    }
}
