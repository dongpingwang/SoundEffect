package com.flyaudio.soundeffect.speaker.logic;

import android.util.SparseArray;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;

import java.util.Locale;

/**
 * @author Dongping Wang
 * date 2020/5/1  22:39
 * email wangdongping@flyaudio.cn
 */
public final class SpeakerVolumeManager {

    private static SparseArray<SparseArray<SpeakerVolumeDataLogic.ChannelVolumeSpeakerBean>> speakerVolumeMap;

    private SpeakerVolumeManager() {
        if (speakerVolumeMap == null) {
            speakerVolumeMap = SpeakerVolumeDataLogic.parserSpeakerVolumeXml();
        }
    }

    private static class InstanceHolder {
        private static SpeakerVolumeManager instance = new SpeakerVolumeManager();
    }

    public static SpeakerVolumeManager getInstance() {
        return InstanceHolder.instance;
    }


    public static final int VOLUME_MIN = -20;
    public static final int VOLUME_MAX = 20;
    /**
     * 记录扬声器音量：收听位置_喇叭位置_扬声器音量
     */
    private static final String KEY_SPEAKER_VOLUME = "key_speaker_volume_%d_%d";

    public void init() {
        int listenPosition = ListenPositionManager.getInstance().getListenPosition();
        for (int speaker : Constants.SPEAKER_TYPES) {
            int volume = getSpeakerVolume(listenPosition, speaker);
        }
    }


    public SparseArray<SparseArray<SpeakerVolumeDataLogic.ChannelVolumeSpeakerBean>> getSpeakerVolumeMap() {
        if (speakerVolumeMap == null) {
            speakerVolumeMap = SpeakerVolumeDataLogic.parserSpeakerVolumeXml();
        }
        return speakerVolumeMap;
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
        int defaultVolume = speakerVolumeMap.get(position).get(speaker).getDefaultValue();
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
    public void setSpeakerVolume(int speaker, int volume) {

    }

    /**
     * 默认的扬声器设置
     */
    public int[] getDefaultSpeakerVolumes(@Constants.ListenPositionType int position) {
        int[] speakerVolumeArr = new int[Constants.SPEAKER_TYPES.length];
        for (int i = 0; i < speakerVolumeArr.length; i++) {
            int speaker = Constants.SPEAKER_TYPES[i];
            speakerVolumeArr[i] = speakerVolumeMap.get(position).get(speaker).getDefaultValue();
        }
        return speakerVolumeArr;
    }
}
