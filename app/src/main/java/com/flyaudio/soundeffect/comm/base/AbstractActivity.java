package com.flyaudio.soundeffect.comm.base;

import com.flyaudio.lib.base.BaseActivity;

/**
 * @author Dongping Wang
 * @date 20-6-8
 * email wangdongping@flyaudio.cn
 */
public abstract class AbstractActivity extends BaseActivity {

    @Override
    protected void init() {
        onInit();
    }

    /**
     * Fragment onViewCreated 回调执行
     */
    protected abstract void onInit();
}
