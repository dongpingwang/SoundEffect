package com.flyaudio.soundeffect.attenuator.activity;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.attenuator.logic.AttenuatorLogic;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.SpeakersLayout;
import com.flyaudio.soundeffect.comm.view.attenuator.AttenuatorAdjuster;
import com.flyaudio.soundeffect.comm.view.attenuator.SoundFieldCoordinateView;
import com.flyaudio.soundeffect.comm.view.attenuator.TouchImageView;

/**
 * @author Dongping Wang
 * @date 20-4-27
 * email wangdongping@flyaudio.cn
 */
public class AttenuatorActivity extends BaseActivity {

    private CommTitleBar titleBar;
    private AttenuatorAdjuster adjuster;
    private SpeakersLayout speakers;
    private TouchImageView touchImageView;

    private AttenuatorLogic attenuatorLogic;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_equilibrium;
    }

    @Override
    protected void init() {
        initTitleBar();
        initTouchImg();
        initAdjuster();
        initSpeakers();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.attenuator_adjust));
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
//                TouchImageView.Position position = touchImageView.getPosition();
//                int x = position.getX() - touchImageView.getPaddingLeft();
//                int y = position.getY() - touchImageView.getPaddingTop();
//                int xStep = (int) (touchImageView.getViewWidth() / 10f + 0.5f);
//                int yStep = (int) (touchImageView.getViewHeight() / 10f + 0.5f);
//                int xSpace = x % xStep;
//                int ySpace = y % yStep;
//                switch (btn) {
//                    case AttenuatorAdjuster.AttenuatorBtn.BTN_UP:
//                        y -= (ySpace == 0) ? yStep : ySpace;
//                        break;
//                    case AttenuatorAdjuster.AttenuatorBtn.BTN_DOWN:
//                        y += (yStep - ySpace);
//                        break;
//                    case AttenuatorAdjuster.AttenuatorBtn.BTN_LEFT:
//                        x -= (xSpace == 0) ? xStep : xSpace;
//                        break;
//                    case AttenuatorAdjuster.AttenuatorBtn.BTN_RIGHT:
//                        x += (xStep - xSpace);
//                        break;
//                    default:
//                        x = (int) (touchImageView.getViewWidth() * 0.5f + 0.5f);
//                        y = (int) (touchImageView.getViewHeight() * 0.5f + 0.5f);
//                }
//                x = x < 0 ? 0 : x > touchImageView.getViewWidth() ? touchImageView.getViewWidth() : x;
//                y = y < 0 ? 0 : y > touchImageView.getViewHeight() ? touchImageView.getViewHeight() : y;
//                touchImageView.setViewPosition(x, y);
//                saveValue();
            }
        });

    }

    private void initTouchImg() {
        touchImageView = getView(R.id.attenuator_touch_iv);
        touchImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                touchImageView.getViewTreeObserver().removeOnPreDrawListener(this);
//                float[] position = attenuatorLogic.getTouchIvValue();
//                int x = (int) (touchImageView.getViewWidth() * position[0] / (position[0] + position[2]) + 0.5f);
//                int y = (int) (touchImageView.getViewHeight() * position[1] / (position[1] + position[3]) + 0.5);
//                touchImageView.setViewPosition(x, y);
                return true;
            }
        });
        touchImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    saveValue();
                }
                return false;
            }
        });

    }

    private void saveValue() {

    }
}
