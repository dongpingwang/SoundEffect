package com.flyaudio.soundeffect.dsp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.dsp.logic.EffectManager;

/**
 * init dsp effect by framework
 *
 * @author Dongping Wang
 * @date 2020.01.06
 */
public class InitEffectService extends IntentService {

    private final static String INIT_DSP_EFFECT = "flyaudio.init.dsp.effect";

    public InitEffectService() {
        super("InitLogicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(), INIT_DSP_EFFECT)) {
                EffectManager.init();
                Logger.d("init dsp effect by framework");
            }
        }

    }
}
