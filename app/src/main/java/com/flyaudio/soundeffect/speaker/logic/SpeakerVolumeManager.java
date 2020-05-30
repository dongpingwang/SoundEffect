package com.flyaudio.soundeffect.speaker.logic;

import android.util.SparseArray;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.dsp.DspHelper;
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
        for (int speaker : Constants.SPEAKER_TYPES) {
            int volume = getSpeakerVolume(listenPosition, speaker);
            setSpeakerVolume(speaker, volume);
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
        int defaultVolume = getSpeakerVolumeMap().get(position).get(speaker).getDefaultValue();
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
        VolumeManager.getInstance().saveVolume(speaker, volume);
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
                DspHelper.getDspHelper().setBalance(sw, volume);
            }
        } else {
            int channel = Constants.SPEAKER_TYPE_MAP.get(speaker);
            DspHelper.getDspHelper().setBalance(channel, volume);
        }
    }
}
