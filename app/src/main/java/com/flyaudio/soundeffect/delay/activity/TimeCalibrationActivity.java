package com.flyaudio.soundeffect.delay.activity;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.NumberSelector;
import com.flyaudio.soundeffect.comm.view.SoundEffectView;
import com.flyaudio.soundeffect.delay.logic.DelayManager;
import com.flyaudio.soundeffect.position.fragment.ListenPositionFragment;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:31
 * email wangdongping@flyaudio.cn
 */
public class TimeCalibrationActivity extends BaseActivity {

    /**
     * 单位为0.1ms , 范围为0-200 (0.1ms)，步进1(0.1ms)
     */
    private static final int DELAY_MAX = 200;
    private static final int DELAY_MIN = 0;
    private static final int SELECTOR_STEP = 1;
    /**
     * 0.0-20.0ms 对比上面的范围，Selector显示上扩大了10倍
     */
    private static final int B = 10;
    private CommTitleBar titleBar;
    private SoundEffectView soundEffectView;
    private DelayManager delayManager;
    private ListenPositionManager listenPositionManager;
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
        delayManager = DelayManager.getInstance();
        listenPositionManager = ListenPositionManager.getInstance();
        listenPosition = listenPositionManager.getListenPosition();
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

            }
        });
    }

    private void initCarSpeakers() {
        soundEffectView = getView(R.id.sound_effect_view);
        // 设置数字显示样式
        soundEffectView.setSelectorValueFormater(new NumberSelector.ValueFormatter() {
            @Override
            public void valueFormat(@NonNull TextView textView, int value) {
                textView.setText(ResUtils.getString(R.string.delay_selector_format, value * 1.0F / B));
            }
        });
        // 设置调节范围
        soundEffectView.setSelectorAdjustRange(DELAY_MIN, DELAY_MAX);
        // 设置步进值
        soundEffectView.setSelectorStep(SELECTOR_STEP);
        // 显示喇叭
        soundEffectView.setSpeakerVisible(BackRowManager.getInstance().isBackRowOn(), SubwooferManager.getInstance().isSubwooferOn());
        // 显示上次的值
        for (int speaker : Constants.SPEAKER_TYPES) {
            int delay = delayManager.getDelay(listenPosition, speaker);
            soundEffectView.setSelectorValue(speaker, delay);
        }
        // 监听NumberSelector
        soundEffectView.setOnSelectorValueChangedListener(new SoundEffectView.OnSelectorValueChangedListener() {
            @Override
            public void onSelectorValueChanged(int positionType, int selectorType, int oldValue, int newValue, boolean byTouch) {
                if (newValue != oldValue && byTouch) {
                    List<Integer> speakersAdjusting = new ArrayList<>();
                    setDelay(positionType, selectorType, newValue);
                    speakersAdjusting.add(selectorType);
                    // 联动调节
                    List<Integer> linkageSpeakers = delayManager.getChannelDelayMap().get(positionType).get(selectorType).getLinkageSpeakers();
                    for (int item : linkageSpeakers) {
                        speakersAdjusting.add(item);
                        soundEffectView.setSelectorValue(item, newValue);
                        setDelay(positionType, item, newValue);
                    }
                    soundEffectView.setAdjusting(speakersAdjusting);
                }

            }
        });
    }

    private void setDelay(@Constants.ListenPositionType int position, @Constants.ListenPositionSpeakerType int speaker, int delay) {
        Logger.d("setDelay: delayValue = " +  delay);
        delayManager.saveDelay(position, speaker, delay);
        delayManager.setDelay(speaker, delay);
    }

}
