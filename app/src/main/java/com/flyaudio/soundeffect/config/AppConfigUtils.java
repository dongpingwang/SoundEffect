package com.flyaudio.soundeffect.config;

import com.flyaudio.lib.config.ConfigManager;
import com.flyaudio.lib.config.loader.AssetConfigLoader;

/**
 * @author Dongping Wang
 * date 2020/5/10  15:37
 * email wangdongping@flyaudio.cn
 */
public final class AppConfigUtils {

    private static final String KEY_DSP_ON = "dsp";

    private AppConfigUtils() {

    }


    public static boolean isDspOn() {
        ConfigManager configManager = ConfigManager.getInstance();
        configManager.addConfigLoader(new AssetConfigLoader());
        return configManager.getBoolean(KEY_DSP_ON, true);
    }
}
