package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2019.12.25
 * email wangdongping@flyaudio.cn
 */
public class SoundEffectView extends FrameLayout {

    private CarSpeakersView carSpeakersView;
    private List<NumberSelector> selectors;
    private OnSelectorValueChangedListener selectorValueChangedListener;

    public SoundEffectView(Context context) {
        this(context, null);
    }

    public SoundEffectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_sound_effect_5_1, this);
        carSpeakersView = (CarSpeakersView) findViewById(R.id.sound_effect_5_1_speaker_view);
        selectors = Arrays.asList(
                (NumberSelector) findViewById(R.id.sound_effect_5_1_left_front_ns),
                (NumberSelector) findViewById(R.id.sound_effect_5_1_right_front_ns),
                (NumberSelector) findViewById(R.id.sound_effect_5_1_left_back_ns),
                (NumberSelector) findViewById(R.id.sound_effect_5_1_right_back_ns),
                (NumberSelector) findViewById(R.id.sound_effect_5_1_subwoofer_ns)
        );
        for (final NumberSelector selector : selectors) {
            selector.setOnNumberChangedListener(new NumberSelector.OnNumberChangedListener() {
                @Override
                public void onNumberChanged(int oldValue, int newValue, boolean byTouch) {
                    if (selectorValueChangedListener != null) {
                        selectorValueChangedListener.onSelectorValueChanged(
                                ListenPositionManager.getInstance().getListenPosition(),
                                Constants.SPEAKER_TYPES[selectors.indexOf(selector)],
                                oldValue, newValue, byTouch);
                    }

                }
            });
        }

    }


    public void setSpeakerVisible(boolean backRowOn, boolean subwooferOn) {
        for (NumberSelector selector : selectors) {
            selector.setEnabled(true);
        }
        int speakers = Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_FRONT_LEFT
                | Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_FRONT_RIGHT;
        if (subwooferOn) {
            speakers = speakers | Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_SUBWOOFER;
        } else {
            selectors.get(4).setEnabled(false);
        }
        if (backRowOn) {
            speakers = speakers | Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_LEFT
                    | Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_RIGHT;
        } else {
            selectors.get(2).setEnabled(false);
            selectors.get(3).setEnabled(false);
        }
        carSpeakersView.setCarSpeakers(speakers);
    }


    public void setSelectorAdjustRange(int min, int max) {
        for (NumberSelector item : selectors) {
            item.setAdjustRange(min, max);
        }
    }

    public void setSelectorValueFormatter(NumberSelector.ValueFormatter valueFormatter) {
        for (NumberSelector numberSelector : selectors) {
            numberSelector.setValueFormatter(valueFormatter);
        }
    }

    public void setDrawableColor(int color) {
        carSpeakersView.setDrawableColor(color);
    }

    public void setSelectorMinValue(int value) {
        for (NumberSelector numberSelector : selectors) {
            numberSelector.setMin(value);
        }
    }

    public void setSelectorMaxValue(int value) {
        for (NumberSelector numberSelector : selectors) {
            numberSelector.setMax(value);
        }
    }

    public void setSelectorStep(int step) {
        for (NumberSelector numberSelector : selectors) {
            numberSelector.setStep(step);
        }
    }

    public int getSelectorValue(@Constants.ListenPositionSpeakerType int selectorType) {
        return selectors.get(indexOfSelector(selectorType)).getValue();
    }

    public void setSelectorValue(@Constants.ListenPositionSpeakerType int selectorType, int value) {
        selectors.get(indexOfSelector(selectorType)).setValue(value);
    }

    public void setAdjusting(List<Integer> selectorTypes) {
        for (NumberSelector numberSelector : selectors) {
            numberSelector.setAdjusting(false);
        }
        for (int i = 0; i < selectorTypes.size(); i++) {
            int speaker = ListenPositionManager.getInstance().speakerType2Index(selectorTypes.get(i));
            if (speaker >= 0 && speaker < selectors.size()) {
                selectors.get(speaker).setAdjusting(true);
            }
        }
    }


    private int indexOfSelector(@Constants.ListenPositionSpeakerType int selectorType) {
        for (int i = 0; i < Constants.SPEAKER_TYPES.length; i++) {
            if (Constants.SPEAKER_TYPES[i] == selectorType) {
                return i;
            }
        }
        return -1;
    }


    public void setOnSelectorValueChangedListener(OnSelectorValueChangedListener listener) {
        selectorValueChangedListener = listener;
    }

    public interface OnSelectorValueChangedListener {
        /**
         * 某个喇叭调节
         *
         * @param positionType 收听位置
         * @param selectorType 喇叭类型
         * @param oldValue     上一个值
         * @param newValue     当前值
         * @param byTouch      是否点击
         */
        void onSelectorValueChanged(@Constants.ListenPositionType int positionType,
                                    @Constants.ListenPositionSpeakerType int selectorType,
                                    int oldValue, int newValue, boolean byTouch);
    }

}
