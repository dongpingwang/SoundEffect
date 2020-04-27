package com.flyaudio.soundeffect.equalizer.activity;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class EqAdjustActivity extends BaseActivity {

    public static final String INTENT_MODE_NAME = "mode_name";

    private CommTitleBar titleBar;
    private EqMode eqMode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_eq_adjust;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
    }

    private void initData() {
        eqMode = getIntent().getParcelableExtra(INTENT_MODE_NAME);
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(eqMode.getName());
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
}
