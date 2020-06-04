package com.flyaudio.soundeffect.trumpet.activity;

import android.view.View;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.attenuator.logic.AttenuatorManager;
import com.flyaudio.soundeffect.comm.view.CommSwitch;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.dsp.service.EffectManager;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

/**
 * @author Dongping Wang
 * date 2020/4/25  23:08
 * email wangdongping@flyaudio.cn
 */
public class TrumpetSettingActivity extends BaseActivity implements View.OnClickListener {

    private CommTitleBar titleBar;
    private CommSwitch switchSubwoofer;
    private CommSwitch switchBackRow;

    private SubwooferManager subwooferManager;
    private BackRowManager backRowManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trumpet_setting;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initSwitch();
    }

    private void initData() {
        subwooferManager = SubwooferManager.getInstance();
        backRowManager = BackRowManager.getInstance();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.trumpet_setting));
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

    private void initSwitch() {
        switchSubwoofer = getView(R.id.switch_subwoofer);
        switchBackRow = getView(R.id.switch_back_row);
        switchSubwoofer.setOnClickListener(this);
        switchBackRow.setOnClickListener(this);
        // 更新上次的状态
        switchSubwoofer.setChecked(!subwooferManager.isSubwooferOn());
        switchBackRow.setChecked(!backRowManager.isBackRowOn());
    }

    @Override
    public void onClick(View view) {
        if (view.equals(switchSubwoofer)) {
            // 调节重低音
            boolean checked = switchSubwoofer.isChecked();
            switchSubwoofer.setChecked(!checked);
            subwooferManager.saveSubwooferState(checked);
            EffectManager.getInstance().setSubwooferEnable();
        } else {
            // 调节后排
            boolean checked = switchBackRow.isChecked();
            switchBackRow.setChecked(!checked);
            backRowManager.saveBackRowState(checked);
            EffectManager.getInstance().setBackRowEnable();
        }
    }
}
