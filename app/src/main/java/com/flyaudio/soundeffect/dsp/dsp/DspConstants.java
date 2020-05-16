package com.flyaudio.soundeffect.dsp.dsp;

/**
 * @author Dongping Wang
 * @date 20-5-7
 * email wangdongping@flyaudio.cn
 */
public class DspConstants {

    /**
     * 通道
     */
    public enum Channel {
        /**
         * 全部通道
         */
        ALL,
        /**
         * 前左通道
         */
        FL,
        /**
         * 前后通道
         */
        FR,
        /**
         * 后左通道
         */
        RL,
        /**
         * 后右通道
         */
        RR,
        /**
         * 左重低音通道
         */
        SWL,
        /**
         * 右重低音通道
         */
        SWR;


        public int getValue() {
            return ordinal();
        }

    }

    /**
     * 高低通滤波控制通道
     */
    public enum EqFilter {
        /**
         * 前排通道
         */
        FRONT,
        /**
         * 后排通道
         */
        REAR,
        /**
         * 重低音通道
         */
        SUBWOOF;

        EqFilter() {
            this.value += 1;
        }

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }


    /**
     * 喇叭输出配置通道
     */
    public enum AudioSpeakerLayout {
        /**
         * 前排输出喇叭
         */
        SPEAKER_LAYOUT_FRONT,
        /**
         * 后排输出喇叭
         */
        SPEAKER_LAYOUT_REAR,
        /**
         * 重低音输出喇叭
         */
        SPEAKER_LAYOUT_SUBWOOF;

        AudioSpeakerLayout() {

        }

        public int getValue() {
            return ordinal();
        }

    }

    /**
     * 返回的结果代码
     */
    public enum ResultCode {
        /**
         * 成功
         */
        SUCCESS(0),
        /**
         * 失败
         */
        FAIL(-1);

        ResultCode(int value) {
            setValue(value);
        }

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

    }

}
