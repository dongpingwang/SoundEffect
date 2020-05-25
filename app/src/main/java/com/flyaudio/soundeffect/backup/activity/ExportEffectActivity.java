package com.flyaudio.soundeffect.backup.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.toast.Toaster;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.lib.utils.TimeUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.bean.Device;
import com.flyaudio.soundeffect.backup.dialog.SelectDiskDialog;
import com.flyaudio.soundeffect.backup.logic.BackupHelper;
import com.flyaudio.soundeffect.backup.logic.UsbManager;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;

import java.util.Date;

/**
 * @author Dongping Wang
 * @date 20-4-27
 * email wangdongping@flyaudio.cn
 */
public class ExportEffectActivity extends BaseActivity implements UsbManager.UsbListener,
        View.OnClickListener, SelectDiskDialog.SelectDiskListener {

    private CommTitleBar titleBar;
    private EditText etFileName;
    private TextView tvFilePath;
    private TextView tvSelectPath;
    private Button btnExportEffect;
    private SelectDiskDialog diskDialog;
    private UsbManager usbManager;
    private Device backupDevice;
    private static final int MSG_BACKUP = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_export_effect;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initFileTitleAndPath();
        initExportView();
    }

    private void initData() {
        usbManager = UsbManager.getInstance();
        usbManager.addUsbListener(this);
        backupDevice = usbManager.checkAllDisk().get(0);
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

        String timeStamp = TimeUtils.date2String(new Date(), "yyyy-MM-dd  HH:mm:ss").replaceAll("\\s| |_|[-]|/|:", "");
        etFileName.setText(ResUtils.getString(R.string.effect_file, timeStamp));
        tvSelectPath.setOnClickListener(this);
        tvFilePath.setText(backupDevice.getDescription());


    }

    private void initExportView() {
        btnExportEffect = getView(R.id.btn_export_effect);
        btnExportEffect.setOnClickListener(this);
    }

    @Override
    public void insert(String path) {

    }

    @Override
    public void eject(String path) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_select_path) {
            if (diskDialog == null) {
                diskDialog = new SelectDiskDialog(context(), usbManager.checkAllDisk());
            }
            diskDialog.show();
            diskDialog.updateAdapter(usbManager.checkAllDisk());
            diskDialog.setListener(this);
        } else if (view.getId() == R.id.btn_export_effect) {
            if (backupDevice != null) {
                backupHandler.sendEmptyMessage(MSG_BACKUP);
            }
        }
    }

    @Override
    public void onSelectDisk(Device device) {
        backupDevice = device;
        tvFilePath.setText(backupDevice.getDescription());
    }

    @SuppressLint("HandlerLeak")
    private final Handler backupHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_BACKUP) {
                boolean success = BackupHelper.backup(backupDevice.getPath(), etFileName.getText().toString().trim());
                if (success) {
                    Toaster.show(ResUtils.getString(R.string.backup_success));
                } else {
                    Toaster.show(ResUtils.getString(R.string.backup_fail));
                }
            }
        }
    };
}
