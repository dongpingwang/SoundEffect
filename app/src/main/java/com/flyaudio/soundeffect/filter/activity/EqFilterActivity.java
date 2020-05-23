package com.flyaudio.soundeffect.filter.activity;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:04
 * email wangdongping@flyaudio.cn
 */
public class EqFilterActivity extends BaseActivity {

    private CommTitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_eq_filter;
    }

    @Override
    protected void init() {
        initTitleBar();
        initTypeSelector();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.eq_filter));
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

    private void initTypeSelector() {

    }
}
