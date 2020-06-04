package com.flyaudio.soundeffect.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.soundeffect.dsp.service.EffectManager;

/**
 * @author Dongping Wang
 * date 2020/6/4  20:49
 * email wangdongping@flyaudio.cn
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String ACTION_FLYAUDIO_BOOT_ANIMATION_COMPLETE = "com.flyaudio.intent.action.BOOT_ANIMATION_COMPLETE";
    private static final String ACTION_FLYAUDIO_ACC_ON = "flyaudio.intent.action.ACC_ON";
    private static final String ACTION_PREBOOT_IPO = "android.intent.action.ACTION_PREBOOT_IPO";
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            Logger.d("BootReceiver onReceive:" + intent.getAction());
            switch (intent.getAction()) {
                case ACTION_FLYAUDIO_BOOT_ANIMATION_COMPLETE:
                case ACTION_FLYAUDIO_ACC_ON:
                case ACTION_PREBOOT_IPO:
                case ACTION_BOOT_COMPLETED:
                    EffectManager.getInstance().init();
                    break;
                default:
            }
        }

    }
}
