package com.flyaudio.soundeffect.delay.logic;

import android.support.annotation.IntRange;
import android.util.SparseArray;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.dsp.logic.DspHelper;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;

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

    public void init() {
        int listenPosition = ListenPositionManager.getInstance().getListenPosition();
        for (int speaker : Constants.SPEAKER_TYPES) {
            int volume = getDelay(listenPosition, speaker);
            setDelay(speaker, volume);
        }
    }

    /**
     * 获取通道延时，值为0-200(单位为0.1ms)
     *
     * @param position 收听位置
     * @param speaker  喇叭
     */
    @IntRange(from = 0, to = 200)
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
                          @Constants.ListenPositionSpeakerType int speaker, @IntRange(from = 0, to = 200) int volume) {
        String spKey = String.format(Locale.getDefault(), KEY_CHANNEL_DELAY, position, speaker);
        SPCacheHelper.getInstance().put(spKey, volume);
    }

    /**
     * 默认的扬声器延时, 单位为0.1ms
     */
    public int[] getDefaultSpeakerDelay(@Constants.ListenPositionType int position) {
        int[] speakerDelayArr = new int[Constants.SPEAKER_TYPES.length];
        for (int i = 0; i < speakerDelayArr.length; i++) {
            int speaker = Constants.SPEAKER_TYPES[i];
            speakerDelayArr[i] = getChannelDelayMap().get(position).get(speaker).getDefaultValue();
        }
        return speakerDelayArr;
    }

    /**
     * 延时设置
     * 注意单位为0.1ms，例如设置前左1ms延时setDelay(DspConstants.Channel.FL, 10)
     *
     * @param speaker 对应的喇叭通道
     * @param delay   延时值(0-200，单位0.1ms)
     */
    public void setDelay(int speaker, @IntRange(from = 0, to = 200) int delay) {
        if (speaker == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_SUBWOOFER) {
            Integer[] swChannels = Constants.SPEAKER_TYPE_MAP_SUBWOOFER.get(speaker);
            for (int sw : swChannels) {
                DspHelper.getDspHelper().setDelay(sw, delay);
            }
        } else {
            int channel = Constants.SPEAKER_TYPE_MAP.get(speaker);
            DspHelper.getDspHelper().setDelay(channel, delay);
        }
    }

}
