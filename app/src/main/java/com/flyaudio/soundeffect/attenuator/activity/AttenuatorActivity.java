package com.flyaudio.soundeffect.attenuator.activity;

import android.view.ViewTreeObserver;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.attenuator.logic.AttenuatorManager;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.SpeakersLayout;
import com.flyaudio.soundeffect.comm.view.attenuator.AttenuatorAdjuster;
import com.flyaudio.soundeffect.comm.view.attenuator.SoundFieldCoordinateView;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;


/**
 * @author Dongping Wang
 * @date 20-4-27
 * email wangdongping@flyaudio.cn
 */
public class AttenuatorActivity extends BaseActivity {

    private CommTitleBar titleBar;
    private AttenuatorAdjuster adjuster;
    private SpeakersLayout speakers;
    private SoundFieldCoordinateView touchImageView;

    private AttenuatorManager attenuatorManager;
    private boolean backRowOn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_attenuator;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initTouchImg();
        initAdjuster();
        initSpeakers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backRowOn = BackRowManager.getInstance().isBackRowOn();
        adjuster.displayIfBackRowOff(backRowOn);
        speakers.displayIfBackRowOff(backRowOn);
        touchImageView.setShowYCoordinate(backRowOn);
        updateTouchValue();
    }

    private void initData() {
        attenuatorManager = AttenuatorManager.getInstance();
        backRowOn = BackRowManager.getInstance().isBackRowOn();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.attenuator_adjust));
        titleBar.updateResetVisibility(false);
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

    private void initSpeakers() {
        speakers = getView(R.id.speakers_layout);
        speakers.setSpeakersEnable(new boolean[]{false, false, false});
    }

    private void initAdjuster() {
        adjuster = getView(R.id.attenuator_adjuster_buttons);
        adjuster.setAdjustListener(new AttenuatorAdjuster.OnAdjustTouchListener() {
            @Override
            public void onTouch(int btn) {
                switch (btn) {
                    case AttenuatorAdjuster.AttenuatorBtn.BTN_UP:
                        touchImageView.changePosY(false);
                        break;
                    case AttenuatorAdjuster.AttenuatorBtn.BTN_DOWN:
                        touchImageView.changePosY(true);
                        break;
                    case AttenuatorAdjuster.AttenuatorBtn.BTN_LEFT:
                        touchImageView.changePosX(false);
                        break;
                    case AttenuatorAdjuster.AttenuatorBtn.BTN_RIGHT:
                        touchImageView.changePosX(true);
                        break;
                    default:
                        touchImageView.goToCenter();
                        break;
                }
            }
        });

    }

    private void initTouchImg() {
        touchImageView = getView(R.id.attenuator_touch_iv);
        touchImageView.setPositionChangedListener(new SoundFieldCoordinateView.PositionChangedListener() {
            @Override
            public void onPositionChanged(int x, int y, boolean byTouch) {
                if (byTouch) {
                    AttenuatorActivity.this.setBalance(x, y);
                }
            }
        });
        updateTouchValue();
    }

    private void updateTouchValue() {
        touchImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] touchValue = attenuatorManager.getTouchValue();
                int x = touchValue[0];
                if (!backRowOn) {
                    x = attenuatorManager.getTouchValueWhenRearOff();
                }
                touchImageView.setPosition(x, touchValue[1]);
            }
        });
    }

    private void setBalance(int x, int y) {
        if (backRowOn) {
            attenuatorManager.saveTouchValue(x, y);
            attenuatorManager.setBalanceXYByWeight(x - 1, y - 1);
        } else {
            attenuatorManager.saveTouchValueWhenRearOff(x);
            attenuatorManager.setXBalanceByWeight(x - 1);
        }
    }
}
