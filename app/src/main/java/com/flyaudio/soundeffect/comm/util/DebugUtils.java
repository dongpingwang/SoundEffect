package com.flyaudio.soundeffect.comm.util;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.manager.LeakCanaryManager;
import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.soundeffect.config.AppPreferences;

/**
 * @author Dongping Wang
 * @date 2019.12.18
 * email  wangdongping@flyaudio.cn
 */
public final class DebugUtils {


    private DebugUtils() {

    }

    public static void debug() {
        setupLog();
        setupLeakCanary();
    }

    private static void setupLog() {
        Logger.setLogFilter(new Logger.LogFilter() {
            @Override
            public boolean isLoggable(int i, int i1, int i2, Object o, Object o1, Object... objects) {
                return AppPreferences.LOGGABLE;
            }
        });
        Logger.setTag(AppPreferences.TAG);
    }


    private static void setupLeakCanary() {
        if (AppPreferences.LOGGABLE_LEAK) {
            LeakCanaryManager leakCanaryManager = LeakCanaryManager.getInstance();
            if (!leakCanaryManager.isInAnalyzerProcess(AppUtils.getContext())) {
                leakCanaryManager.install();
            }
        }
    }

}
