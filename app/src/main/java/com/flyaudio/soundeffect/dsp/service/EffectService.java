package com.flyaudio.soundeffect.dsp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.flyaudio.soundeffect.attenuator.logic.AttenuatorManager;
import com.flyaudio.soundeffect.delay.logic.DelayManager;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.filter.bean.EqFilterParam;
import com.flyaudio.soundeffect.filter.logic.EqFilterManager;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.speaker.logic.SpeakerVolumeManager;
import com.flyaudio.soundeffect.speaker.logic.VolumeManager;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;
import com.flyaudio.soundeffect.trumpet.logic.TrumpetManager;

/**
 * @author Dongping Wang
 * @date 2020.01.06
 */
public class EffectService extends IntentService {

    private static final int SPEAKER_FRONT_LEFT = 0;

    public EffectService() {
        super("InitLogicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(), Actions.EFFECT_ACTION)) {
                int type = intent.getIntExtra(Actions.EFFECT_EXTRA, Actions.EXTRA_INIT);
                switch (type) {
                    case Actions.EXTRA_INIT:
                        init();
                        break;
                    case Actions.EXTRA_SET_EQ:
                        setEq();
                        break;
                    case Actions.EXTRA_SET_ALL_EQ:
                        setAllEq();
                        break;
                    case Actions.EXTRA_SET_LISTEN_POSITION:
                        setListenPosition();
                        break;
                    case Actions.EXTRA_SET_DELAY:
                        setDelay(intent);
                        break;
                    case Actions.EXTRA_SET_SPEAKER_VOLUME:
                        setSpeakerVolume(intent);
                        break;
                    case Actions.EXTRA_SET_BALANCE:
                        setBalance(intent);
                        break;
                    case Actions.EXTRA_SET_TRUMPET_SUBWOOFER:
                        setSubwooferEnable();
                        break;
                    case Actions.EXTRA_SET_TRUMPET_SUBWOOFER_REVERSE:
                        setSubooferReverse();
                        break;
                    case Actions.EXTRA_SET_TRUMPET_BACK_ROW:
                        setBackRowEnable();
                        break;
                    default:
                        setEqFilter(intent);
                        break;
                }

            }
        }
    }

    private void init() {
        EqManager.getInstance().init();
        DelayManager.getInstance().init();
        VolumeManager.getInstance().init();
        TrumpetManager.getInstance().init();
        EqFilterManager.getInstance().init();
    }

    private void setEq() {
        EqDataBean data = EqManager.getInstance().getEqModeData(EqManager.getInstance().getCurrentEq());
        int region = data.current;
        EqManager.getInstance().setEq(region, data.frequencies[region], data.qValues[region], data.gains[region]);
    }

    private void setAllEq() {
        EqManager.getInstance().init();
    }

    private void setListenPosition() {
        ListenPositionManager.getInstance().setListenPosition();
    }

    private void setDelay(Intent intent) {
        int speaker = intent.getIntExtra(Actions.EXTRA_SPEAKER, SPEAKER_FRONT_LEFT);
        int value = intent.getIntExtra(Actions.EXTRA_DELAY_VALUE, 0);
        DelayManager.getInstance().setDelay(speaker, value);
    }

    private void setBalance(Intent intent) {
        int speaker = intent.getIntExtra(Actions.EXTRA_SPEAKER, SPEAKER_FRONT_LEFT);
        int value = intent.getIntExtra(Actions.EXTRA_BALANCE_VALUE, 0);
        AttenuatorManager.getInstance().setBalance(speaker, value);
    }

    private void setSpeakerVolume(Intent intent) {
        int speaker = intent.getIntExtra(Actions.EXTRA_SPEAKER, SPEAKER_FRONT_LEFT);
        int value = intent.getIntExtra(Actions.EXTRA_SPEAKER_VOLUME_VALUE, 0);
        SpeakerVolumeManager.getInstance().setSpeakerVolume(speaker, value);
    }


    private void setSubwooferEnable() {
        SubwooferManager.getInstance().setSubwooferEnable(SubwooferManager.getInstance().isSubwooferOn());
    }

    private void setSubooferReverse() {
        SubwooferManager.getInstance().setSubooferReverse(SubwooferManager.getInstance().isSubwooferReverse());
    }

    private void setBackRowEnable() {
        BackRowManager.getInstance().setBackRowEnable(BackRowManager.getInstance().isBackRowOn());
    }

    private void setEqFilter(Intent intent) {
        EqFilterParam eqFilterParam = intent.getParcelableExtra(Actions.EXTRA_EQ_FILTER_VALUE);
        if (eqFilterParam != null) {
            EqFilterManager.getInstance().setEqFilter(eqFilterParam.channel, eqFilterParam.freq, eqFilterParam.slope, eqFilterParam.enable);
        }
    }
}

