package com.flyaudio.soundeffect.test.activity;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.toast.DebugToast;
import com.flyaudio.soundeffect.config.AppConfigUtils;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;


/**
 * @author Dongping Wang
 * date 2020/5/10  13:15
 * email wangdongping@flyaudio.cn
 */
public class TestActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {

        DebugToast.show(AppConfigUtils.isDspOn() + "aaa");

        return 0;
    }

    @Override
    protected void init() {

    }
}
