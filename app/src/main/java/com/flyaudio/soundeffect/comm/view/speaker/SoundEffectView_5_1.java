package com.flyaudio.soundeffect.comm.view.speaker;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.comm.NumberSelector;
import java.util.Arrays;
import java.util.List;

public class SoundEffectView_5_1 extends FrameLayout {

    private CarSpeakersView carSpeakersView;
    private List<NumberSelector> selectors;
    private LinearLayout llAdjustRule;
    public ImageView ivAdjustRule;
    public TextView tvRuleName;
    private OnSelectorValueChangedListener selectorValueChangedListener;
    private IInputValueListener mInputValueListener;
    public SoundEffectView_5_1(Context context) {
        this(context, null);
    }

    public SoundEffectView_5_1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundEffectView_5_1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_sound_effect_5_1, this);
        findView();
        setSpeakerVisibleType();
        for (final NumberSelector selector : selectors) {
            selector.setOnNumberChangedListener(new NumberSelector.OnNumberChangedListener() {
                @Override
                public void onNumberChanged(int oldValue, int newValue, boolean byTouch) {
                    if (selectorValueChangedListener != null)
                        selectorValueChangedListener.onSelectorValueChanged(getListenerPosition(),
                                ListenPositionLogic.SPEAKER_TYPES[selectors.indexOf(selector)],
                                oldValue, newValue, byTouch);
                }
            });
        }


    }

    private void findView() {
        carSpeakersView = getView(R.id.sound_effect_5_1_speaker_view);
        selectors = Arrays.asList(
                (NumberSelector) getView(R.id.sound_effect_5_1_left_front_ns),
                (NumberSelector) getView(R.id.sound_effect_5_1_right_front_ns),
                (NumberSelector) getView(R.id.sound_effect_5_1_left_back_ns),
                (NumberSelector) getView(R.id.sound_effect_5_1_right_back_ns),
                (NumberSelector) getView(R.id.sound_effect_5_1_subwoofer_ns)
        );
        llAdjustRule = getView(R.id.ll_adjust_rule);
        ivAdjustRule = getView(R.id.iv_adjust_rule);
        tvRuleName = getView(R.id.tv_rule_name);
    }

    public void hideRuleAdjust() {
        llAdjustRule.setVisibility(View.GONE);
    }


    public void setSpeakerVisibleType() {
        boolean subwooferOn = SubwooferLogic.getInstance().isSubwooferOn();
        boolean backRowOn = RearSpeakerLogic.getInstance().getRearSpeaker();
        for (NumberSelector selector:selectors) {
            selector.setEnabled(true);
        }
        int speakers = ListenPositionLogic.LISTEN_POSITION_SPEAKER_LEFT_FRONT | ListenPositionLogic.LISTEN_POSITION_SPEAKER_RIGHT_FRONT;
        if (subwooferOn) {
            speakers = speakers | ListenPositionLogic.LISTEN_POSITION_SPEAKER_SUBWOOFER;
        } else {
            selectors.get(4).setEnabled(false);
        }
        if (backRowOn) {
            speakers = speakers | ListenPositionLogic.LISTEN_POSITION_SPEAKER_LEFT_BACK | ListenPositionLogic.LISTEN_POSITION_SPEAKER_RIGHT_BACK;
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

    private <T extends View> T getView(@IdRes int id) {
        return (T) findViewById(id);
    }


    public void setSelectorValueFormater(NumberSelector.ValueFormater valueFormater) {
        for (NumberSelector numberSelector : selectors)
            numberSelector.setValueFormater(valueFormater);
    }

    public void setDrawableColor(int color) {
        carSpeakersView.setDrawableColor(color);
    }


    public void setSelectorMinValue(int value) {
        for (NumberSelector numberSelector : selectors)
            numberSelector.setMin(value);
    }

    public void setSelectorMaxValue(int value) {
        for (NumberSelector numberSelector : selectors)
            numberSelector.setMax(value);
    }

    public void setSelectorStep(int step) {
        for (NumberSelector numberSelector : selectors)
            numberSelector.setStep(step);
    }

    public int getSelectorValue(@ListenPositionLogic.ListenPositionSpeakerType int selectorType) {
        return selectors.get(indexOfSelector(selectorType)).getValue();
    }

    public void setSelectorValue(@ListenPositionLogic.ListenPositionSpeakerType int selectorType, int value) {
        selectors.get(indexOfSelector(selectorType)).setValue(value);
    }

    private int indexOfSelector(@ListenPositionLogic.ListenPositionSpeakerType int selectorType) {
        for (int i = 0; i < ListenPositionLogic.SPEAKER_TYPES.length; i++)
            if (ListenPositionLogic.SPEAKER_TYPES[i] == selectorType)
                return i;
        return -1;
    }


    @ListenPositionLogic.ListenPositionType
    public int getListenerPosition() {
        return SoundFieldLogic.getInstance().getListenPosition();
    }


    public void setOnSelectorValueChangedListener(OnSelectorValueChangedListener listener) {
        selectorValueChangedListener = listener;
    }

    public interface OnSelectorValueChangedListener {
        void onSelectorValueChanged(@ListenPositionLogic.ListenPositionType int positionType,
                                    @ListenPositionLogic.ListenPositionSpeakerType int selectorType,
                                    int oldValue, int newValue, boolean byTouch);
    }

    public void setAdjusting(List<Integer>selectorTypes) {
        for (NumberSelector numberSelector : selectors) {
             numberSelector.setAdjusting(false);
        }
        for (int i = 0; i < selectorTypes.size() ; i++) {
            int speaker = ListenPositionLogic.indexOfListenPositionSpeakerType(selectorTypes.get(i));
            if (speaker >= 0 && speaker < selectors.size()) {
                selectors.get(speaker).setAdjusting(true);
            }
        }
    }

    public void setInputValueListener(IInputValueListener listener){
        mInputValueListener = listener;
    }
    public interface  IInputValueListener{
        void value(@ListenPositionLogic.ListenPositionSpeakerType int selectorType, int oldValue);
    }
}
