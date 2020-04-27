package com.flyaudio.soundeffect.trumpet.activity;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:08
 * email wangdongping@flyaudio.cn
 */
public class TrumpetSettingActivity extends BaseActivity {

    private CommTitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trumpet_setting;
    }

    @Override
    protected void init() {
        initTitleBar();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.trumpet_setting));
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
