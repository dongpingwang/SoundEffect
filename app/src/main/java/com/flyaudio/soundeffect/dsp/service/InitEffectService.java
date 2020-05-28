package com.flyaudio.soundeffect.dsp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.delay.logic.DelayManager;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.filter.logic.EqFilterManager;
import com.flyaudio.soundeffect.speaker.logic.VolumeManager;
import com.flyaudio.soundeffect.trumpet.logic.TrumpetManager;

/**
 * @author Dongping Wang
 * @date 2020.01.06
 */
public class InitEffectService extends IntentService {

    public static String INIT_DSP_EFFECT = "flyaudio.init.dsp.effect";

    public InitEffectService() {
        super("InitLogicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(), INIT_DSP_EFFECT)) {
                init();
                Logger.d("init dsp effect");
            }
        }
    }

    public void init() {
        EqManager.getInstance().init();
        DelayManager.getInstance().init();
        VolumeManager.getInstance().init();
        TrumpetManager.getInstance().init();
        EqFilterManager.getInstance().init();
    }

}

