package com.flyaudio.soundeffect.comm.util;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/4/28  0:15
 * email wangdongping@flyaudio.cn
 */
public final class EqUtils {
    private EqUtils() {

    }

    public static String getFreq2Str(int frequency) {
        String title;
        String suffix;
        if (frequency < 1000) {
            title = String.valueOf(frequency);
            suffix = ResUtils.getString(R.string.hz_unit);
        } else {
            suffix = ResUtils.getString(R.string.khz_unit);
            String temp = String.valueOf(frequency);
            int i0 = temp.indexOf("0");
            if (i0 == 1) {
                if (temp.length() == 4) {
                    title = temp.substring(0, 1);
                } else {
                    title = temp.substring(0, 2);
                }
            } else if (i0 == 2) {
                if (temp.length() == 4) {
                    title = temp.substring(0, 1) + "." + temp.substring(1, 2);
                } else {
                    title = temp.substring(0, 2);
                }
            } else { // i0 == 3
                if (temp.length() == 4) {
                    title = temp.substring(0, 1) + "." + temp.substring(1, 3);
                } else {
                    title = temp.substring(0, 2) + "." + temp.substring(2, 3);
                }
            }

        }
        return title + suffix;
    }
}
