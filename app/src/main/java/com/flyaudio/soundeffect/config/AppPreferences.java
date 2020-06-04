package com.flyaudio.soundeffect.config;

import android.os.Build;
import android.text.TextUtils;

/**
 * @author Dongping Wang
 * date 2020/5/10  14:50
 * email wangdongping@flyaudio.cn
 */
public final class AppPreferences {

    public static final String TAG = "SoundEffect";
    public static final boolean LOGGABLE = true;
    public static final boolean LOGGABLE_LEAK = true;
    public static final boolean LOGGABLE_DSP = true;
    public static final boolean USER_DEBUG = TextUtils.equals(Build.TYPE, "userdebug");


    private AppPreferences() {

    }

}
