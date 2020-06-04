package com.flyaudio.soundeffect.main.event;

/**
 * @author Dongping Wang
 * @date 20-6-4
 * email wangdongping@flyaudio.cn
 */
public interface Event {


    String RESTORE_DATA = "restore_data";

    /**
     * 回调事件
     *
     * @param tag 相应的事件
     */
    void onEvent(String tag);
}
