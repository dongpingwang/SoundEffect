package com.flyaudio.soundeffect.dsp.logic;

import android.content.Intent;

import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.soundeffect.dsp.service.InitEffectService;

/**
 * @author Dongping Wang
 * date 2020/5/1  18:02
 * email wangdongping@flyaudio.cn
 */
public final class EffectManager {

    private EffectManager() {

    }

    public static void init() {
        AppUtils.getContext().startService(new Intent()
                .setAction(InitEffectService.INIT_DSP_EFFECT)
                .setPackage(AppUtils.getPackageName()));
    }

}
