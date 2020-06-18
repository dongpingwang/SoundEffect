package com.flyaudio.soundeffect.position.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.base.AbstractFragment;
import com.flyaudio.soundeffect.comm.view.ListenPositionButtons;
import com.flyaudio.soundeffect.comm.view.SpeakersLayout;
import com.flyaudio.soundeffect.delay.activity.TimeCalibrationActivity;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.main.event.Event;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.speaker.activity.SpeakerVolumeActivity;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;

/**
 * @author Dongping Wang
 * date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class ListenPositionFragment extends AbstractFragment implements View.OnClickListener,
        ListenPositionButtons.ListenPositionCheckedListener {

    public static final String INTENT_POSITION_NAME = "position_name";

    private TextView tvSpeakerAdjust;
    private TextView tvDelayAdjust;
    private ListenPositionButtons buttons;
    private SpeakersLayout speakers;
    private static ListenPositionManager listenPositionManager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_listen_position;
    }

    @Override
    protected void onInit() {
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        buttons.displayIfBackRowOff(BackRowManager.getInstance().isBackRowOn());
        speakers.displayIfBackRowOff(BackRowManager.getInstance().isBackRowOn());
    }

    private void initView() {
        buttons = getView(R.id.btn_listen_position);
        speakers = getView(R.id.speakers_layout);
        tvSpeakerAdjust = getView(R.id.tv_speaker);
        tvDelayAdjust = getView(R.id.tv_delay);

        speakers.setSpeakerImage(ResUtils.getDrawable(R.drawable.comm_speaker2));
        buttons.setListenPositionCheckedListener(this);
        tvSpeakerAdjust.setOnClickListener(this);
        tvDelayAdjust.setOnClickListener(this);
    }

    private void initData() {
        listenPositionManager = ListenPositionManager.getInstance();
        int listenPosition = listenPositionManager.getListenPosition();
        buttons.setChecked(listenPositionManager.listenPosition2Index(listenPosition));
        speakers.setSpeakersEnable(listenPositionManager.listenPosition2SpeakerStatus(listenPosition));
    }

    @Override
    public void onCheckedChanged(int index) {
        int listenPosition = listenPositionManager.index2ListenPosition(index);
        listenPositionManager.saveListenPosition(listenPosition);
        EffectManager.getInstance().setListenPosition();
        speakers.setSpeakersEnable(listenPositionManager.listenPosition2SpeakerStatus(listenPosition));
        // 关闭时不能调节喇叭音量和通道延时
        boolean closePosition = listenPositionManager.isClosePosition(listenPosition);
        tvSpeakerAdjust.setEnabled(!closePosition);
        tvDelayAdjust.setEnabled(!closePosition);
    }

    @Override
    public void onClick(View view) {
        Class<?> clazz;
        if (view.getId() == R.id.tv_speaker) {
            clazz = SpeakerVolumeActivity.class;
        } else {
            clazz = TimeCalibrationActivity.class;
        }
        Intent intent = new Intent(context(), clazz);
        intent.putExtra(INTENT_POSITION_NAME, buttons.getPositionName());
        startActivity(intent);
    }

    @Override
    public void onEvent(String event) {
        if (TextUtils.equals(event, Event.RESTORE_DATA)) {
            initData();
        }
    }
}
