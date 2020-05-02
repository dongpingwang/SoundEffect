package com.flyaudio.soundeffect.speaker.activity;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.NumberSelector;
import com.flyaudio.soundeffect.comm.view.SoundEffectView;
import com.flyaudio.soundeffect.position.fragment.ListenPositionFragment;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.speaker.logic.SpeakerVolumeManager;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:06
 * email wangdongping@flyaudio.cn
 */
public class SpeakerVolumeActivity extends BaseActivity {

    public static final int VOLUME_MIN = -20;
    public static final int VOLUME_MAX = 20;

    private CommTitleBar titleBar;
    private SoundEffectView soundEffectView;
    private SpeakerVolumeManager speakerVolumeManager;
    private static int listenPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_speaker_volume;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initCarSpeakers();
    }

    private void initData() {
        speakerVolumeManager = SpeakerVolumeManager.getInstance();
        listenPosition = ListenPositionManager.getInstance().getListenPosition();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(getIntent().getStringExtra(ListenPositionFragment.INTENT_POSITION_NAME));
        titleBar.setListener(new CommTitleBar.TitleBarActionListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onReset() {
                int[] defaultSpeakerVolumes = speakerVolumeManager.getDefaultSpeakerVolumes(listenPosition);
                for (int i = 0; i < defaultSpeakerVolumes.length; i++) {
                    setVolume(listenPosition, ListenPositionManager.getInstance().index2SpeakerType(i), defaultSpeakerVolumes[i]);
                    soundEffectView.setSelectorValue(ListenPositionManager.getInstance().index2SpeakerType(i), defaultSpeakerVolumes[i]);
                }
            }
        });
    }

    private void initCarSpeakers() {
        soundEffectView = getView(R.id.sound_effect_view);
        // 设置数字显示样式
        soundEffectView.setSelectorValueFormater(new NumberSelector.ValueFormatter() {
            @Override
            public void valueFormat(@NonNull TextView textView, int value) {
                textView.setText(ResUtils.getString(R.string.speaker_volume_selector_format, value));
            }
        });
        // 设置调节范围
        soundEffectView.setSelectorAdjustRange(VOLUME_MIN, VOLUME_MAX);
        // 显示喇叭
        soundEffectView.setSpeakerVisible(BackRowManager.getInstance().isBackRowOn(), SubwooferManager.getInstance().isSubwooferOn());
        // 显示上次的值
        for (int speaker : Constants.SPEAKER_TYPES) {
            int volume = speakerVolumeManager.getSpeakerVolume(listenPosition, speaker);
            soundEffectView.setSelectorValue(speaker, volume);
        }
        // 监听NumberSelector
        soundEffectView.setOnSelectorValueChangedListener(new SoundEffectView.OnSelectorValueChangedListener() {
            @Override
            public void onSelectorValueChanged(int positionType, int selectorType, int oldValue, int newValue, boolean byTouch) {
                if (newValue != oldValue && byTouch) {
                    List<Integer> speakersAdjusting = new ArrayList<>();
                    setVolume(positionType, selectorType, newValue);
                    speakersAdjusting.add(selectorType);
                    // 联动调节
                    List<Integer> linkageSpeakers = speakerVolumeManager.getSpeakerVolumeMap().get(positionType).get(selectorType).getLinkageSpeakers();
                    for (int item : linkageSpeakers) {
                        speakersAdjusting.add(item);
                        soundEffectView.setSelectorValue(item, newValue);
                        setVolume(positionType, item, newValue);
                    }
                    soundEffectView.setAdjusting(speakersAdjusting);
                }

            }
        });
    }

    private void setVolume(@Constants.ListenPositionType int position, @Constants.ListenPositionSpeakerType int speaker, int volume) {
        speakerVolumeManager.saveSpeakerVolume(position, speaker, volume);
        speakerVolumeManager.setSpeakerVolume(speaker, volume);
    }

}

