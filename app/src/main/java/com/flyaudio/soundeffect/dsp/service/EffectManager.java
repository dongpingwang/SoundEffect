package com.flyaudio.soundeffect.dsp.service;

import android.content.Intent;

import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.soundeffect.dsp.dsp.DspConstants;
import com.flyaudio.soundeffect.dsp.dsp.DspHelper;
import com.flyaudio.soundeffect.dsp.dsp.check.IDspCheck;
import com.flyaudio.soundeffect.filter.bean.EqFilterParam;

/**
 * @author Dongping Wang
 * date 2020/5/1  18:02
 * email wangdongping@flyaudio.cn
 */
public final class EffectManager implements IDspCheck.DspServiceConnection {


    private EffectManager() {
        DspHelper.getDspCheckHelper().setAutoConnect(true);
    }

    private static class InstanceHolder {
        private static EffectManager instance = new EffectManager();
    }

    public static EffectManager getInstance() {
        return InstanceHolder.instance;
    }


    @Override
    public void onServiceConnected() {
        startService(Actions.EXTRA_INIT);
    }

    @Override
    public void onServiceDisconnected() {

    }

    public void init() {
        if (DspHelper.getDspCheckHelper().getDspInitState() == DspConstants.ResultCode.SUCCESS.getValue()) {
            startService(Actions.EXTRA_INIT);
        } else {
            DspHelper.getDspCheckHelper().registerServiceConnection(this);
        }
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

    public void setEqFilter(EqFilterParam param) {
        Intent intent = getIntent(Actions.EXTRA_SET_EQ_FILTER)
                .putExtra(Actions.EXTRA_EQ_FILTER_VALUE, param);
        AppUtils.getContext().startService(intent);

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
