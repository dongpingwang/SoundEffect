package com.flyaudio.soundeffect.comm.util;

/**
 * @author Dongping Wang
 * date 2020/4/28  11:30
 * email wangdongping@flyaudio.cn
 */
public class SpConvertUtils {

    private static final String SEPARATOR = ",";

    public static String array2String(int[] arr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i < arr.length - 1) {
                builder.append(SEPARATOR);
            }
        }
        return builder.toString();
    }

    public static int[] string2array(String str) {
        String[] strArr = str.split(SEPARATOR);
        int[] intArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            intArr[i] = Integer.parseInt(strArr[i]);
        }
        return intArr;
    }

}
