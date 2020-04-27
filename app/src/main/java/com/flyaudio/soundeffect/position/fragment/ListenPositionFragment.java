package com.flyaudio.soundeffect.position.fragment;

import android.content.Intent;
import android.view.View;

import com.flyaudio.lib.base.BaseFragment;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.ListenPositionButtons;
import com.flyaudio.soundeffect.comm.view.SpeakersLayout;
import com.flyaudio.soundeffect.delay.TimeCalibrationActivity;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.speaker.SpeakerVolumeActivity;

/**
 * @author Dongping Wang
 * date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class ListenPositionFragment extends BaseFragment implements View.OnClickListener,
        ListenPositionButtons.ListenPositionCheckedListener {

    private ListenPositionButtons buttons;
    private SpeakersLayout speakers;
    private ListenPositionManager listenPositionManager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_listen_position;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        buttons = getView(R.id.btn_listen_position);
        speakers = getView(R.id.speakers_layout);
        buttons.setListenPositionCheckedListener(this);
        getView(R.id.tv_speaker).setOnClickListener(this);
        getView(R.id.tv_delay).setOnClickListener(this);
    }

    private void initData() {
        listenPositionManager = ListenPositionManager.getInstance();
        int listenPosition = listenPositionManager.getListenPosition();
        buttons.setChecked(listenPositionManager.listenPosition2Index(listenPosition));
        speakers.setSpeakersEnable(listenPositionManager.listenPosition2SpeakerStatus(listenPosition));
    }


    @Override
    public void onCheckedChanged(int index) {
        listenPositionManager.saveListenPosition(listenPositionManager.index2ListenPosition(index));
        speakers.setSpeakersEnable(listenPositionManager.listenPosition2SpeakerStatus(listenPositionManager.index2ListenPosition(index)));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_speaker) {
            startActivity(new Intent(context(), SpeakerVolumeActivity.class));
        } else if (view.getId() == R.id.tv_delay) {
            startActivity(new Intent(context(), TimeCalibrationActivity.class));
        }
    }


}
