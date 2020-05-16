package com.flyaudio.soundeffect.config;

import com.flyaudio.lib.config.ConfigManager;
import com.flyaudio.lib.config.loader.AssetConfigLoader;

/**
 * @author Dongping Wang
 * date 2020/5/10  15:37
 * email wangdongping@flyaudio.cn
 */
public final class AppConfigUtils {

    private static final String ASSET_DIR = "file:///android_assets/";
    private static final String CONFIG_FILE = "config.json";

    /**
     * DSP是否打开，方便调试
     */
    private static final String KEY_DSP_ON = "dsp";
    private static final boolean VALUE_DSP_ON = true;

    private AppConfigUtils() {

    }

    public static boolean isDspOn() {
        ConfigManager configManager = ConfigManager.getInstance();
        AssetConfigLoader assetConfigLoader = new AssetConfigLoader();
        configManager.addConfigLoader(assetConfigLoader);
        configManager.loadConfig(ASSET_DIR + CONFIG_FILE);
        boolean dsp = configManager.getBoolean(KEY_DSP_ON, VALUE_DSP_ON);
        configManager.removeConfigLoader(assetConfigLoader);
        return dsp;
    }
}
