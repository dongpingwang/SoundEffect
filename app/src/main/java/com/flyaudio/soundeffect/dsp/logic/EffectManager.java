package com.flyaudio.soundeffect.dsp.logic;

import com.flyaudio.lib.thread.TaskQueue;
import com.flyaudio.soundeffect.delay.logic.DelayManager;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.filter.logic.EqFilterManager;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.speaker.logic.VolumeManager;
import com.flyaudio.soundeffect.trumpet.logic.TrumpetManager;

/**
 * @author Dongping Wang
 * date 2020/5/1  18:02
 * email wangdongping@flyaudio.cn
 */
public final class EffectManager {

    private EffectManager() {

    }

    public static void init() {
        final TaskQueue taskQueue = new TaskQueue();
        taskQueue.post(new Runnable() {
            @Override
            public void run() {
                initEffect();
                taskQueue.removeTask(this);
                taskQueue.clearTaskQueue();
                taskQueue.release();
            }
        });

    }

    private static void initEffect() {
        EqManager.getInstance().init();
        DelayManager.getInstance().init();
        VolumeManager.getInstance().init();
        TrumpetManager.getInstance().init();
        EqFilterManager.getInstance().init();
    }

}
