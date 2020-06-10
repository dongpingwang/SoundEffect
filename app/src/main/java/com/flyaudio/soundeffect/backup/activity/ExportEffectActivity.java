package com.flyaudio.soundeffect.backup.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyaudio.lib.async.AsyncWorker;
import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.toast.Toaster;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.lib.utils.TimeUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.bean.Device;
import com.flyaudio.soundeffect.backup.dialog.SelectDiskDialog;
import com.flyaudio.soundeffect.backup.logic.BackupManager;
import com.flyaudio.soundeffect.backup.logic.UsbManager;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.ProgressStateView;

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
    private ProgressStateView progressStateView;
    private SelectDiskDialog diskDialog;
    private UsbManager usbManager;
    private Device backupDevice;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbManager.removeUsbListener(this);
    }

    private void initData() {
        usbManager = UsbManager.getInstance();
        usbManager.addUsbListener(this);
        backupDevice = usbManager.getAllDisk().get(0);
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

        String timeStamp = TimeUtils.date2String(new Date(), "yyyyMMddHHmmss");
        etFileName.setText(ResUtils.getString(R.string.effect_file, timeStamp));
        tvSelectPath.setOnClickListener(this);
        tvFilePath.setText(backupDevice.getDescription());


    }

    private void initExportView() {
        btnExportEffect = getView(R.id.btn_backup_effect);
        progressStateView = getView(R.id.progress_state);
        btnExportEffect.setOnClickListener(this);
        progressStateView.setVisibility(View.GONE);
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
                diskDialog = new SelectDiskDialog(context(), usbManager.getAllDisk());
            }
            diskDialog.show();
            diskDialog.updateAdapter(usbManager.getAllDisk());
            diskDialog.setListener(this);
        } else if (view.getId() == R.id.btn_backup_effect) {
            if (backupDevice != null) {
                backup();
            }
        }
    }

    private void backup() {
        BackupManager.getInstance().backupAsync(backupDevice.getPath(), etFileName.getText().toString().trim(),
                new AsyncWorker.ResultCallback<Boolean>() {
                    @Override
                    public void onStart(AsyncWorker.Cancellable cancellable) {
                        updateViewVisibility(true, ResUtils.getString(R.string.exporting));
                    }

                    @Override
                    public void onFinish(Boolean result) {
                        if (result) {
                            progressStateView.complete(ResUtils.getString(R.string.exported));
                        } else {
                            onError(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toaster.show(ResUtils.getString(R.string.backup_fail));
                        updateViewVisibility(false, "");
                    }
                });
    }

    @Override
    public void onSelectDisk(Device device) {
        backupDevice = device;
        tvFilePath.setText(backupDevice.getDescription());
    }

    private void updateViewVisibility(boolean progressing, String hintText) {
        progressStateView.setVisibility(progressing ? View.VISIBLE : View.GONE);
        btnExportEffect.setVisibility(progressing ? View.GONE : View.VISIBLE);
        if (progressing) {
            progressStateView.progressing(hintText);
        } else {
            progressStateView.complete(hintText);
        }
    }
}
