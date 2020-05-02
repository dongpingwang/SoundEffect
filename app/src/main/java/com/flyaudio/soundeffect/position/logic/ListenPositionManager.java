package com.flyaudio.soundeffect.position.logic;

import com.flyaudio.lib.sp.SPCacheHelper;

import static com.flyaudio.soundeffect.position.logic.Constants.ListenPositionType.*;

/**
 * @author Dongping Wang
 * date 2020/4/25  22:05
 * email wangdongping@flyaudio.cn
 */
public final class ListenPositionManager {

    /**
     * 记录收听位置：1-前左 2-前右  3-前排  12-后排   15-全部   0-关闭
     */
    private static final String KEY_LISTEN_POSITION = "key_listen_position";


    private ListenPositionManager() {

    }

    private static class InstanceHolder {
        private static ListenPositionManager instance = new ListenPositionManager();
    }

    public static ListenPositionManager getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 获取收听位置
     */
    public int getListenPosition() {
        return SPCacheHelper.getInstance().getInt(KEY_LISTEN_POSITION, Constants.ListenPositionType.LISTEN_POSITION_ALL);
    }

    /**
     * 保存收听位置
     */
    public void saveListenPosition(@Constants.ListenPositionType int listenPosition) {
        SPCacheHelper.getInstance().put(KEY_LISTEN_POSITION, listenPosition);
    }

    public int listenPosition2Index(@Constants.ListenPositionType int listenPosition) {
        return Constants.indexOfListenPosition(listenPosition);
    }

    public int speakerType2Index(@Constants.ListenPositionSpeakerType int speaker) {
        return Constants.indexOfListenPositionSpeakerType(speaker);
    }

    public int index2ListenPosition(int index) {
        int position = Constants.ListenPositionType.LISTEN_POSITION_ALL;
        if (index >= 0 && index < Constants.LISTEN_POSITIONS.length) {
            position = Constants.LISTEN_POSITIONS[index];
        }
        return position;
    }

    public int index2SpeakerType(int index) {
        int seaker = Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_FRONT_LEFT;
        if (index >= 0 && index < Constants.SPEAKER_TYPES.length) {
            seaker = Constants.SPEAKER_TYPES[index];
        }
        return seaker;
    }

    public boolean[] listenPosition2SpeakerStatus(@Constants.ListenPositionType int listenPosition) {
        boolean frontLeft, frontRight, backRow;
        frontLeft = frontRight = backRow = false;
        if (listenPosition == LISTEN_POSITION_DRIVER_SEAT) {
            frontLeft = true;
        } else if (listenPosition == LISTEN_POSITION_COPILOT_SEAT) {
            frontRight = true;
        } else if (listenPosition == LISTEN_POSITION_FRONT_ROW) {
            frontLeft = frontRight = true;
        } else if (listenPosition == LISTEN_POSITION_BACK_ROW) {
            backRow = true;
        } else if (listenPosition == LISTEN_POSITION_ALL) {
            frontLeft = frontRight = backRow = true;
        } else {
            frontLeft = frontRight = backRow = false;
        }
        return new boolean[]{frontLeft, frontRight, backRow};
    }
}
