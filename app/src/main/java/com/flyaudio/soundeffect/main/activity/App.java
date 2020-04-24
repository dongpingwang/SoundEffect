package com.flyaudio.soundeffect.main.activity;

import com.flyaudio.lib.base.BaseApplication;
import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.soundeffect.comm.util.DebugUtils;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class App extends BaseApplication {

    @Override
    protected void init() {
        AppUtils.init(this);
        DebugUtils.debug();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
