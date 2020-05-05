package com.flyaudio.soundeffect.delay.logic;

import android.util.SparseArray;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.position.logic.Constants;

import java.util.Locale;

/**
 * @author Dongping Wang
 * date 2020/5/1  22:42
 * email wangdongping@flyaudio.cn
 */
public final class DelayManager {

    /**
     * 记录通道延时：收听位置_喇叭位置_通道延时
     */
    private static final String KEY_CHANNEL_DELAY = "channel_delay_%d_%d";

    private SparseArray<SparseArray<ChannelDelayMapLogic.ChannelDelaySpeakerBean>> channelDelayMap;

    private DelayManager() {

    }

    private static class InstanceHolder {
        private static DelayManager instance = new DelayManager();
    }

    public static DelayManager getInstance() {
        return InstanceHolder.instance;
    }


    public SparseArray<SparseArray<ChannelDelayMapLogic.ChannelDelaySpeakerBean>> getChannelDelayMap() {
        if (channelDelayMap == null) {
            channelDelayMap = ChannelDelayMapLogic.getChannelDelayMap();
        }
        return channelDelayMap;
    }

    /**
     * 获取通道延时，值为0-200(单位为0.1ms)
     *
     * @param position 收听位置
     * @param speaker  喇叭
     */
    public int getDelay(@Constants.ListenPositionType int position,
                        @Constants.ListenPositionSpeakerType int speaker) {
        String spKey = String.format(Locale.getDefault(), KEY_CHANNEL_DELAY, position, speaker);
        int defaultVolume = getChannelDelayMap().get(position).get(speaker).getDefaultValue();
        return SPCacheHelper.getInstance().getInt(spKey, defaultVolume);
    }

    /**
     * 保存通道延时，值为0-200(单位为0.1ms)
     *
     * @param position 收听位置
     * @param speaker  喇叭
     * @param volume   延时
     */
    public void saveDelay(@Constants.ListenPositionType int position,
                          @Constants.ListenPositionSpeakerType int speaker, int volume) {
        String spKey = String.format(Locale.getDefault(), KEY_CHANNEL_DELAY, position, speaker);
        SPCacheHelper.getInstance().put(spKey, volume);
    }

    public void setDelay(int speaker, int delay) {

    }

    /**
     * 默认的扬声器设置
     */
    public int[] getDefaultSpeakerVolumes(@Constants.ListenPositionType int position) {
        int[] speakerVolumeArr = new int[Constants.SPEAKER_TYPES.length];
        for (int i = 0; i < speakerVolumeArr.length; i++) {
            int speaker = Constants.SPEAKER_TYPES[i];
            speakerVolumeArr[i] = getChannelDelayMap().get(position).get(speaker).getDefaultValue();
        }
        return speakerVolumeArr;
    }
}
