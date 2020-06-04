package com.flyaudio.soundeffect.dsp.dsp.check;

/**
 * @author Dongping Wang
 * @date 20-6-3
 * email wangdongping@flyaudio.cn
 */
public interface IDspCheck {

    /**
     * 获取dsp远程服务的初始化状态
     *
     * @return 0代表初始化完成
     */
    int getDspInitState();

    /**
     * 设置自动去连接Dsp远程服务
     *
     * @param autoConnect 是否自动连接
     */
    void setAutoConnect(boolean autoConnect);

    /**
     * 注册dsp远程服务连接监听
     *
     * @param connection 监听回调
     */
    void registerServiceConnection(DspServiceConnection connection);

    /**
     * 反注册dsp远程服务连接监听
     *
     * @param connection 监听回调
     */
    void unregisterServiceConnection(DspServiceConnection connection);

    interface DspServiceConnection {
        /**
         * dsp远程服务连接成功
         */
        void onServiceConnected();

        /**
         * dsp远程服务断开连接
         */
        void onServiceDisconnected();
    }
}
