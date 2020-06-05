package com.flyaudio.soundeffect.speaker.logic;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;

import java.util.Locale;

/**
 * 处理扬声器音量和衰减平衡音量的叠加效果
 *
 * @author Dongping Wang
 * @date 20-5-27
 * email wangdongping@flyaudio.cn
 */
public final class VolumeManager {

    /**
     * 记录调节衰减平衡和扬声器调节后的喇叭音量
     */
    private static final String KEY_VOLUME = "volume_%d";

    private VolumeManager() {

    }

    private static class InstanceHolder {
        private static VolumeManager instance = new VolumeManager();
    }

    public static VolumeManager getInstance() {
        return InstanceHolder.instance;
    }


    public void init() {
        for (int speaker : Constants.SPEAKER_TYPES) {
            SpeakerVolumeManager.getInstance().setSpeakerVolume(speaker, getVolume(speaker));
        }
    }


    public void saveVolume(@Constants.ListenPositionSpeakerType int speaker, int volume) {
        String spKey = String.format(Locale.getDefault(), KEY_VOLUME, speaker);
        SPCacheHelper.getInstance().put(spKey, volume);
    }

    public int getVolume(@Constants.ListenPositionSpeakerType int speaker) {
        // 默认是扬声器的音量
        String spKey = String.format(Locale.getDefault(), KEY_VOLUME, speaker);
        int defaultValue = SpeakerVolumeManager.getInstance().getSpeakerVolumeMap().
                get(ListenPositionManager.getInstance().getListenPosition()).get(speaker).getDefaultValue();
        return SPCacheHelper.getInstance().getInt(spKey, defaultValue);
    }

}
