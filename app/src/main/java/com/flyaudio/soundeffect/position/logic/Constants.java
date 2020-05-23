package com.flyaudio.soundeffect.position.logic;

import android.support.annotation.IntDef;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.flyaudio.soundeffect.dsp.dsp.DspConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import static com.flyaudio.soundeffect.position.logic.Constants.ListenPositionSpeakerType.*;
import static com.flyaudio.soundeffect.position.logic.Constants.ListenPositionType.*;

/**
 * @author Dongping Wang
 * date 2020/4/25  22:08
 * email wangdongping@flyaudio.cn
 */
public final class Constants {

    private Constants() {

    }

    @IntDef({
            LISTEN_POSITION_SPEAKER_FRONT_LEFT,
            LISTEN_POSITION_SPEAKER_FRONT_RIGHT,
            LISTEN_POSITION_SPEAKER_BACK_LEFT,
            LISTEN_POSITION_SPEAKER_BACK_RIGHT,
            LISTEN_POSITION_SPEAKER_SUBWOOFER
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ListenPositionSpeakerType {
        int LISTEN_POSITION_SPEAKER_NONE = 0x00;
        /**
         * 前左喇叭
         */
        int LISTEN_POSITION_SPEAKER_FRONT_LEFT = 0x01;
        /**
         * 前右喇叭
         */
        int LISTEN_POSITION_SPEAKER_FRONT_RIGHT = 0x01 << 1;
        /**
         * 后左喇叭
         */
        int LISTEN_POSITION_SPEAKER_BACK_LEFT = 0x01 << 2;
        /**
         * 后右喇叭
         */
        int LISTEN_POSITION_SPEAKER_BACK_RIGHT = 0x01 << 3;
        /**
         * 重低音喇叭
         */
        int LISTEN_POSITION_SPEAKER_SUBWOOFER = 0x01 << 4;
    }

    @IntDef({
            LISTEN_POSITION_DRIVER_SEAT,
            LISTEN_POSITION_COPILOT_SEAT,
            LISTEN_POSITION_FRONT_ROW,
            LISTEN_POSITION_BACK_ROW,
            LISTEN_POSITION_ALL,
            LISTEN_POSITION_CLOSE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ListenPositionType {
        /**
         * 收听驾驶位
         */
        int LISTEN_POSITION_DRIVER_SEAT = LISTEN_POSITION_SPEAKER_FRONT_LEFT;
        /**
         * 收听副驾驶位
         */
        int LISTEN_POSITION_COPILOT_SEAT = LISTEN_POSITION_SPEAKER_FRONT_RIGHT;
        /**
         * 收听前排
         */
        int LISTEN_POSITION_FRONT_ROW = LISTEN_POSITION_DRIVER_SEAT | LISTEN_POSITION_COPILOT_SEAT;
        /**
         * 收听后排
         */
        int LISTEN_POSITION_BACK_ROW = LISTEN_POSITION_SPEAKER_BACK_LEFT | LISTEN_POSITION_SPEAKER_BACK_RIGHT;
        /**
         * 收听全部
         */
        int LISTEN_POSITION_ALL = LISTEN_POSITION_FRONT_ROW | LISTEN_POSITION_BACK_ROW;
        /**
         * 关闭收听
         */
        int LISTEN_POSITION_CLOSE = LISTEN_POSITION_SPEAKER_NONE;
    }


    public final static int[] SPEAKER_TYPES = {
            LISTEN_POSITION_SPEAKER_FRONT_LEFT,
            LISTEN_POSITION_SPEAKER_FRONT_RIGHT,
            LISTEN_POSITION_SPEAKER_BACK_LEFT,
            LISTEN_POSITION_SPEAKER_BACK_RIGHT,
            LISTEN_POSITION_SPEAKER_SUBWOOFER
    };

    public final static int[] LISTEN_POSITIONS = {
            LISTEN_POSITION_DRIVER_SEAT,
            LISTEN_POSITION_COPILOT_SEAT,
            LISTEN_POSITION_FRONT_ROW,
            LISTEN_POSITION_BACK_ROW,
            LISTEN_POSITION_ALL,
            LISTEN_POSITION_CLOSE
    };

    static int indexOfListenPosition(@ListenPositionType int listenPosition) {
        for (int i = 0; i < LISTEN_POSITIONS.length; i++) {
            if (listenPosition == LISTEN_POSITIONS[i]) {
                return i;
            }
        }
        return -1;
    }

    static int indexOfListenPositionSpeakerType(@ListenPositionSpeakerType int speaker) {
        for (int i = 0; i < SPEAKER_TYPES.length; i++) {
            if (speaker == SPEAKER_TYPES[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 前左、前右、后左、后右喇叭与底层参数对应
     */
    public static final SparseIntArray SPEAKER_TYPE_MAP = new SparseIntArray() {{
        put(LISTEN_POSITION_SPEAKER_FRONT_LEFT, DspConstants.Channel.FL.getValue());
        put(LISTEN_POSITION_SPEAKER_FRONT_RIGHT, DspConstants.Channel.FR.getValue());
        put(LISTEN_POSITION_SPEAKER_BACK_LEFT, DspConstants.Channel.RL.getValue());
        put(LISTEN_POSITION_SPEAKER_BACK_RIGHT, DspConstants.Channel.RR.getValue());
        put(LISTEN_POSITION_SPEAKER_SUBWOOFER, DspConstants.Channel.SWL.getValue());
        put(LISTEN_POSITION_SPEAKER_SUBWOOFER, DspConstants.Channel.SWR.getValue());
    }};

    /**
     * (左/右)重低音喇叭与底层参数对应
     */
    public static final SparseArray<Integer[]> SPEAKER_TYPE_MAP_SUBWOOFER = new SparseArray<Integer[]>() {
        {
            put(LISTEN_POSITION_SPEAKER_SUBWOOFER, new Integer[]{DspConstants.Channel.SWL.getValue(), DspConstants.Channel.SWR.getValue()});
        }
    };
}
