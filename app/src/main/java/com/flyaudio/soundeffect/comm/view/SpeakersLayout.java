package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.flyaudio.soundeffect.R;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/4/25  21:34
 * email wangdongping@flyaudio.cn
 */
public class SpeakersLayout extends FrameLayout {

    private List<SpeakerImageView> speakers;

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
        speakers = Arrays.asList((SpeakerImageView) findViewById(R.id.speaker_front_left),
                (SpeakerImageView) findViewById(R.id.speaker_front_right),
                (SpeakerImageView) findViewById(R.id.speaker_back_row));
    }

    public void displayIfBackRowOff(boolean on) {
        speakers.get(2).setVisibility(on ? VISIBLE : GONE);
    }

    public void setSpeakersEnable(boolean[] speakerStatus) {
        for (int i = 0; i < speakers.size(); i++) {
            speakers.get(i).setEnabled(speakerStatus[i]);
        }
    }

    public void setSpeakerImage(Drawable speakerImage) {
        for (SpeakerImageView speaker : speakers) {
            speaker.setDrawable(speakerImage);
        }
    }
}
