package com.flyaudio.soundeffect.comm.base;

import com.flyaudio.lib.base.BaseFragment;
import com.flyaudio.soundeffect.main.event.Event;
import com.flyaudio.soundeffect.main.event.EventPoster;

/**
 * @author Dongping Wang
 * @date 20-6-8
 * email wangdongping@flyaudio.cn
 */
public abstract class AbstractFragment extends BaseFragment implements Event {

    @Override
    protected void init() {
        EventPoster.getDefaultPoster().addEventController(this);
        onInit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventPoster.getDefaultPoster().removeEventController(this);
    }

    @Override
    public void onEvent(String tag) {

    }

    /**
     * Fragment onViewCreated 回调执行
     */
    protected abstract void onInit();
}
