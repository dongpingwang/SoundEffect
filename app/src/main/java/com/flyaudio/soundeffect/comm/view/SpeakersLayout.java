package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.flyaudio.soundeffect.R;

/**
 * @author Dongping Wang
 * date 2020/4/25  21:34
 * email wangdongping@flyaudio.cn
 */
public class SpeakersLayout extends FrameLayout {

    private SpeakerImageView speakerFrontLeft;
    private SpeakerImageView speakerFrontRight;
    private SpeakerImageView speakerBackRow;

    public SpeakersLayout(@NonNull Context context) {
        this(context, null);
    }

    public SpeakersLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeakersLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_speakers_layout, this);
        speakerFrontLeft = (SpeakerImageView) findViewById(R.id.speaker_front_left);
        speakerFrontRight = (SpeakerImageView) findViewById(R.id.speaker_front_right);
        speakerBackRow = (SpeakerImageView) findViewById(R.id.speaker_back_row);
    }

    public void displayIfBackRowOff(boolean on) {
        speakerBackRow.setVisibility(on ? VISIBLE : GONE);
    }

    public void setSpeakersEnable(boolean[] speakerStatus) {
        if (speakerStatus != null && speakerStatus.length == 3) {
            speakerFrontLeft.setEnabled(speakerStatus[0]);
            speakerFrontRight.setEnabled(speakerStatus[1]);
            speakerBackRow.setEnabled(speakerStatus[2]);
        }
    }
}
