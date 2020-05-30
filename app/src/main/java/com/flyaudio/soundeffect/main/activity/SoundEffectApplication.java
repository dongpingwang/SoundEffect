package com.flyaudio.soundeffect.main.activity;

import com.flyaudio.lib.async.AsyncWorker;
import com.flyaudio.lib.base.BaseApplication;
import com.flyaudio.soundeffect.comm.util.DebugUtils;
import com.flyaudio.soundeffect.dsp.service.EffectManager;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class SoundEffectApplication extends BaseApplication {

    @Override
    protected void init() {
        AsyncWorker.execute(new AsyncWorker.BackgroundTask() {
            @Override
            public void doInBackground() {
                DebugUtils.debug();
                EffectManager.getInstance().init();
            }
        });

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
