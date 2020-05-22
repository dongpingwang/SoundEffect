package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
    private LinearLayout llSpeakersRoot;

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
        llSpeakersRoot = (LinearLayout) findViewById(R.id.ll_speakers_root);
        speakers = Arrays.asList((SpeakerImageView) findViewById(R.id.speaker_front_left),
                (SpeakerImageView) findViewById(R.id.speaker_front_right),
                (SpeakerImageView) findViewById(R.id.speaker_back_row));
    }

    public void displayIfBackRowOff(boolean on) {
        ViewGroup.LayoutParams layoutParams = llSpeakersRoot.getLayoutParams();
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.speakers_layout_size);
        if (on) {
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.speakers_layout_size);
        } else {
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.speakers_layout_size_no_rear);
        }
        llSpeakersRoot.setLayoutParams(layoutParams);
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
