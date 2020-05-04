package com.flyaudio.soundeffect.backup.activity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;

/**
 * @author Dongping Wang
 * @date 20-4-27
 * email wangdongping@flyaudio.cn
 */
public class ExportEffectActivity extends BaseActivity {

    private CommTitleBar titleBar;
    private EditText etFileName;
    private TextView tvFilePath;
    private TextView tvSelectPath;
    private Button btnExportEffect;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_export_effect;
    }

    @Override
    protected void init() {
        initTitleBar();
        initFileTitleAndPath();
        initExportView();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.updateResetVisibility(false);
        titleBar.setTitleName(ResUtils.getString(R.string.export_effect));
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

    private void initFileTitleAndPath() {
        etFileName = getView(R.id.et_name);
        tvFilePath = getView(R.id.tv_file_path);
        tvSelectPath = getView(R.id.tv_select_path);
    }

    private void initExportView() {
        btnExportEffect = getView(R.id.btn_export_effect);
    }

}
