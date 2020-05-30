package com.flyaudio.soundeffect.dsp.service;

import android.content.Intent;
import com.flyaudio.lib.utils.AppUtils;

/**
 * @author Dongping Wang
 * date 2020/5/1  18:02
 * email wangdongping@flyaudio.cn
 */
public final class EffectManager {


    private EffectManager() {

    }

    private static class InstanceHolder {
        private static EffectManager instance = new EffectManager();
    }

    public static EffectManager getInstance() {
        return InstanceHolder.instance;
    }

    public void init() {
        startService(Actions.EXTRA_INIT);
    }

    public void setEq() {
        startService(Actions.EXTRA_SET_EQ);
    }

    public void setAllEq() {
        startService(Actions.EXTRA_SET_ALL_EQ);
    }

    public void setListenPosition() {
        startService(Actions.EXTRA_SET_LISTEN_POSITION);
    }

    public void setDelay(int speaker, int delayValue) {
        Intent intent = getIntent(Actions.EXTRA_SET_DELAY)
                .putExtra(Actions.EXTRA_SPEAKER, speaker)
                .putExtra(Actions.EXTRA_DELAY_VALUE, delayValue);
        AppUtils.getContext().startService(intent);
    }

    public void setBalance(int speaker, int balanceValue) {
        Intent intent = getIntent(Actions.EXTRA_SET_BALANCE)
                .putExtra(Actions.EXTRA_SPEAKER, speaker)
                .putExtra(Actions.EXTRA_BALANCE_VALUE, balanceValue);
        AppUtils.getContext().startService(intent);
    }

    public void setSubwooferEnable() {
        startService(Actions.EXTRA_SET_TRUMPET_SUBWOOFER);
    }

    public void setSubooferReverse() {
        startService(Actions.EXTRA_SET_TRUMPET_SUBWOOFER_REVERSE);
    }

    public void setBackRowEnable() {
        startService(Actions.EXTRA_SET_TRUMPET_BACK_ROW);
    }

    public void setEqFilter() {
        startService(Actions.EXTRA_SET_EQ_FILTER);
    }

    private void startService(int effectType) {
        AppUtils.getContext().startService(getIntent(effectType));
    }

    private Intent getIntent(int effectType) {
        return new Intent(AppUtils.getContext(), EffectService.class)
                .setAction(Actions.EFFECT_ACTION)
                .putExtra(Actions.EFFECT_EXTRA, effectType);
    }
}
