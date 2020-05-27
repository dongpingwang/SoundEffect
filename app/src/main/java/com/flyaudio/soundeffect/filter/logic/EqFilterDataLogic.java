package com.flyaudio.soundeffect.filter.logic;

import android.content.res.XmlResourceParser;
import android.util.SparseArray;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

import org.xmlpull.v1.XmlPullParser;

import java.math.BigDecimal;

/**
 * @author Dongping Wang
 * @date 2020/3/1615:22
 * email wangdongping@flyaudio.cn
 */
public final class EqFilterDataLogic {

    /**
     * 高通频率
     */
    static final int[] HPF = new int[]{50, 63, 80, 100, 125, 160, 200};
    /**
     * 低通频率(50-200 ，步进5)
     */
    private static final int LPF_MAX = 200;
    private static final int LPF_MIN = 50;
    private static final int LPF_STEP = 5;
    /**
     * 分频斜率
     */
    static final int[] SLOPES = new int[]{6, 12, 18};
    private static final boolean LOOP = false;

    private EqFilterDataLogic() {

    }


    public static int getHpf(int current, boolean up) {
        int result = current;
        if (up) {
            for (int item : HPF) {
                if (item > current) {
                    result = item;
                    break;
                }
            }
        } else {
            for (int i = HPF.length - 1; i >= 0; i--) {
                if (HPF[i] < current) {
                    result = HPF[i];
                    break;
                }
            }
        }
        if (result == current && LOOP) {
            result = up ? HPF[0] : HPF[HPF.length - 1];
        }
        return result;
    }

    public static int getLpf(int current, boolean up) {
        int result = current;
        if (up) {
            result += LPF_STEP;
            if (result > LPF_MAX) {
                result = LOOP ? LPF_MIN : current;
            }
        } else {
            result -= LPF_STEP;
            if (result < LPF_MIN) {
                result = LOOP ? LPF_MAX : current;
            }
        }
        return result;
    }

    public static int getSlope(int current, boolean up) {
        int result = current;
        if (up) {
            for (int item : SLOPES) {
                if (item > current) {
                    result = item;
                    break;
                }
            }
        } else {
            for (int i = SLOPES.length - 1; i >= 0; i--) {
                if (SLOPES[i] < current) {
                    result = SLOPES[i];
                    break;
                }
            }
        }
        if (result == current && LOOP) {
            result = up ? SLOPES[0] : SLOPES[SLOPES.length - 1];
        }
        return result;
    }


    public static int limitValue(double value, boolean isSubwoofer) {
        if (isSubwoofer && value % 5 > 2.5f) {
            return (int) (value + (5 - value % 5));
        }
        if (isSubwoofer) {
            return (int) (value - (value % 5));
        }

        if (value < 56) {
            return 50;
        }
        if (value < 71) {
            return 63;
        }
        if (value < 90) {
            return 80;
        }
        if (value < 112) {
            return 100;
        }
        if (value < 142) {
            return 125;
        }
        if (value < 180) {
            return 160;
        }
        return 200;
    }


    public static int limitAngle(double angle) {
        if (angle < 30) {
            return 15;
        }
        if (angle < 60) {
            return 45;
        }
        return 75;
    }

    public static int angle2Slope(double angle) {
        if (angle < 30) {
            return 6;
        }
        if (angle < 60) {
            return 12;
        }
        return 18;
    }

    public static double slope2Angle(int slope) {
        if (slope < 9) {
            return 15;
        }
        if (slope < 15) {
            return 45;
        }
        return 75;
    }

    public static SparseArray<Double> getSlopEqValue() {
        SparseArray<Double> maps = new SparseArray<>();
        XmlResourceParser parser = ResUtils.getXml(R.xml.eq_slope_q_value);
        try {
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    if ("item".equals(parser.getName())) {
                        int name = parser.getAttributeIntValue(null, "name", -1);
                        float qValue = parser.getAttributeFloatValue(null, "q", 0.0F);
                        BigDecimal bigDecimal = new BigDecimal(String.valueOf(qValue));
                        maps.put(name, bigDecimal.doubleValue());
                    }
                }
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parser.close();
        return maps;
    }
}
