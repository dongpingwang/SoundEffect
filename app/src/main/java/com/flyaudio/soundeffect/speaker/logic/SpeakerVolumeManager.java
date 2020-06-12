package com.flyaudio.soundeffect.speaker.logic;

import android.util.SparseArray;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.dsp.DspHelper;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

import java.util.Locale;

/**
 * @author Dongping Wang
 * date 2020/5/1  22:39
 * email wangdongping@flyaudio.cn
 */
public final class SpeakerVolumeManager {

    public static final int VOLUME_MIN = -20;
    public static final int VOLUME_MAX = 12;

    private static SparseArray<SparseArray<SpeakerVolumeDataLogic.ChannelVolumeSpeakerBean>> speakerVolumeMap;

    private SpeakerVolumeManager() {

    }


    private static class InstanceHolder {
        private static SpeakerVolumeManager instance = new SpeakerVolumeManager();
    }

    public static SpeakerVolumeManager getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 记录扬声器音量：收听位置_喇叭位置_扬声器音量
     */
    private static final String KEY_SPEAKER_VOLUME = "speaker_volume_%d_%d";

    public void init() {
        int listenPosition = ListenPositionManager.getInstance().getListenPosition();
        boolean subwooferOn = SubwooferManager.getInstance().isSubwooferOn();
        for (int speaker : Constants.SPEAKER_TYPES) {
            int volume = getSpeakerVolume(listenPosition, speaker);
            if (speaker == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_SUBWOOFER && !subwooferOn) {
                volume = VOLUME_MIN;
            }
            setSpeakerVolume(speaker, volume);
        }
    }

    public SparseArray<SparseArray<SpeakerVolumeDataLogic.ChannelVolumeSpeakerBean>> getSpeakerVolumeMap() {
        if (speakerVolumeMap == null) {
            speakerVolumeMap = SpeakerVolumeDataLogic.parserSpeakerVolumeXml();
        }
        return speakerVolumeMap;
    }

    public void setSpeakerVolumeIfSubwooferOff(boolean enable) {
        int position = ListenPositionManager.getInstance().getListenPosition();
        int speaker = Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_SUBWOOFER;
        int volume;
        if (enable) {
            volume = getSpeakerVolume(position, speaker);
        } else {
            volume = VOLUME_MIN;
        }
        EffectManager.getInstance().setSpeakerVolume(speaker, volume);

    }


    /**
     * 获取喇叭音量
     *
     * @param position 收听位置
     * @param speaker  喇叭
     */
    public int getSpeakerVolume(@Constants.ListenPositionType int position,
                                @Constants.ListenPositionSpeakerType int speaker) {
        String spKey = String.format(Locale.getDefault(), KEY_SPEAKER_VOLUME, position, speaker);
        int defaultVolume;
        try {
            defaultVolume = getSpeakerVolumeMap().get(position).get(speaker).getDefaultValue();
        } catch (Exception e) {
            defaultVolume = 0;
        }
        return SPCacheHelper.getInstance().getInt(spKey, defaultVolume);
    }

    /**
     * 保存喇叭音量
     *
     * @param position 收听位置
     * @param speaker  喇叭
     * @param volume   音量
     */
    public void saveSpeakerVolume(@Constants.ListenPositionType int position,
                                  @Constants.ListenPositionSpeakerType int speaker, int volume) {
        String spKey = String.format(Locale.getDefault(), KEY_SPEAKER_VOLUME, position, speaker);
        SPCacheHelper.getInstance().put(spKey, volume);
    }

    /**
     * 默认的扬声器设置
     */
    public int[] getDefaultSpeakerVolumes(@Constants.ListenPositionType int position) {
        int[] speakerVolumeArr = new int[Constants.SPEAKER_TYPES.length];
        for (int i = 0; i < speakerVolumeArr.length; i++) {
            int speaker = Constants.SPEAKER_TYPES[i];
            speakerVolumeArr[i] = getSpeakerVolumeMap().get(position).get(speaker).getDefaultValue();
        }
        return speakerVolumeArr;
    }

    public void setSpeakerVolume(@Constants.ListenPositionSpeakerType int speaker, int volume) {
        if (speaker == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_SUBWOOFER) {
            Integer[] swChannels = Constants.SPEAKER_TYPE_MAP_SUBWOOFER.get(speaker);
            for (int sw : swChannels) {
                DspHelper.getDspHelper().setChannelVolume(sw, volume);
            }
        } else {
            int channel = Constants.SPEAKER_TYPE_MAP.get(speaker);
            DspHelper.getDspHelper().setChannelVolume(channel, volume);
        }
    }
}
