package com.flyaudio.soundeffect.dsp.dsp;

/**
 * @author Dongping Wang
 * date 2020/5/1  17:51
 * email wangdongping@flyaudio.cn
 */
public interface IDsp {
    /**
     * 均衡器设置：通过设置每个频段增益值和Q值，补偿高、中、低音频率
     *
     * @param channel 通道类型
     * @param region  0 ~12 //对应频段
     * @param freq    频率
     * @param q       Q值
     * @param gain    增益值
     * @return 0代表设置成功
     * @see DspConstants.Channel
     */
    int setEq(int channel, int region, double freq, double q, double gain);

    /**
     * 高低通滤波设置：通过设置滤波器类型、频点、斜率，滤出所需要高频或低频信号
     *
     * @param channel 高低通滤波通道类型
     * @param type
     * @param freq    频率
     * @param q       Q值
     * @param gain    增益值
     * @param enable  是否可用
     * @return 0代表设置成功
     * @see DspConstants.EqFilter
     */
    int setEqFilter(int channel, int type, double freq, double q, double gain, boolean enable);

    /**
     * 喇叭配置：配置喇叭输出开关
     *
     * @param channel 通道类型
     * @param enable  是否可用
     * @return 0代表设置成功
     * @see DspConstants.AudioSpeakerLayout
     */
    int setChannel(int channel, boolean enable);

    /**
     * 延时设置：设置各通道延时输出时间
     * 注意单位为0.1ms，例如设置前左1ms延时setDelay(DspConstants.Channel.FL, 10)
     *
     * @param channel    通道类型
     * @param delayValue 延时值
     * @return 0代表设置成功
     * @see DspConstants.Channel
     */
    int setDelay(int channel, int delayValue);


    /**
     * 音量控制：输出通道音量控制
     *
     * @param channel      声道选择
     * @param balanceValue 声道增益值
     * @return 0代表设置成功
     * @see DspConstants.Channel
     */
    int setBalance(int channel, double balanceValue);

    /**
     * 通道正反向控制:通道正反向调节
     *
     * @param channel 声道选择
     * @param reverse true代表反向，false代表正想
     * @return 0代表设置成功
     * @see DspConstants.Channel
     */
    int setDspPhaseSwitch(int channel, boolean reverse);

}
