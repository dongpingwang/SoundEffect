package com.flyaudio.soundeffect.backup.activity;

import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.flyaudio.lib.adapter.RecyclerViewAdapter;
import com.flyaudio.lib.async.AsyncWorker;
import com.flyaudio.lib.base.BaseActivity;
import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.toast.Toaster;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.adapter.EffectListAdapter;
import com.flyaudio.soundeffect.backup.bean.EffectFile;
import com.flyaudio.soundeffect.backup.logic.BackupManager;
import com.flyaudio.soundeffect.backup.logic.ScanManager;
import com.flyaudio.soundeffect.backup.logic.UsbManager;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.comm.view.ProgressStateView;
import com.flyaudio.soundeffect.main.event.EventContent;

import java.io.File;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-4-27
 * email wangdongping@flyaudio.cn
 */
public class ImportEffectActivity extends BaseActivity implements UsbManager.UsbListener,
        View.OnClickListener, RecyclerViewAdapter.OnItemClickListener {

    private CommTitleBar titleBar;
    private RecyclerView rvFileList;
    private Button btnSearch;
    private ProgressStateView progressStateView;
    private UsbManager usbManager;
    private EffectListAdapter adapter;
    @StringRes
    private int state;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_effect;
    }

    @Override
    protected void init() {
        initData();
        initTitleBar();
        initSearchFiles();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbManager.removeUsbListener(this);
    }

    private void initData() {
        usbManager = UsbManager.getInstance();
        usbManager.addUsbListener(this);
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.updateResetVisibility(false);
        titleBar.setTitleName(ResUtils.getString(R.string.import_effect));
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

    private void initSearchFiles() {
        btnSearch = getView(R.id.btn_backup_effect);
        rvFileList = getView(R.id.rv_effect_file_list);
        progressStateView = getView(R.id.progress_state);
        progressStateView.setVisibility(View.GONE);
        state = R.string.search_effect_file;
        btnSearch.setText(state);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void insert(String path) {

    }

    @Override
    public void eject(String path) {

    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnSearch)) {
            if (state == R.string.search_effect_file) {
                searchEffectFiles();
            } else {
                restore();
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        adapter.updateSelect(position);
    }

    private void restore() {
        EffectFile currentEffect = adapter.getCurrentEffect();
        if (currentEffect != null) {
            File sourceFile = new File(currentEffect.getPath());
            BackupManager.getInstance().restoreAsync(sourceFile, new AsyncWorker.ResultCallback<Boolean>() {
                @Override
                public void onStart(AsyncWorker.Cancellable cancellable) {
                    updateViewVisibility(true, ResUtils.getString(R.string.importing));
                }

                @Override
                public void onFinish(Boolean result) {
                    updateViewVisibility(true, "");
                    progressStateView.complete(ResUtils.getString(R.string.imported));
                    if (result) {
                        EventContent.sendDataRestore();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Toaster.show(ResUtils.getString(R.string.backup_fail));
                }
            });
        }

    }

    private void searchEffectFiles() {
        ScanManager.getInstance().scanEffectFileAsync(new AsyncWorker.ResultCallback<List<EffectFile>>() {
            @Override
            public void onStart(AsyncWorker.Cancellable cancellable) {
                updateViewVisibility(true, ResUtils.getString(R.string.searching));

            }

            @Override
            public void onFinish(List<EffectFile> result) {
                initAdapter(result);
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("searchEffectFiles: error!! ");
                e.printStackTrace();
            }
        });
    }

    private void initAdapter(List<EffectFile> files) {
        if (!files.isEmpty()) {
            files.get(0).setChecked(true);
        }
        if (adapter == null) {
            adapter = new EffectListAdapter(context(), files);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context(), LinearLayoutManager.VERTICAL, false);
            rvFileList.setAdapter(adapter);
            rvFileList.setLayoutManager(layoutManager);
            adapter.setOnItemClickListener(this);
        } else {
            adapter.updateAdapter(files);
        }
        if (adapter.getItemViewCount() <= 0) {
            updateViewVisibility(false, ResUtils.getString(R.string.no_effect_file_and_try_later));
            progressStateView.setVisibility(View.VISIBLE);
        } else {
            updateViewVisibility(false, "");
            state = R.string.import_effect_file;
            btnSearch.setText(state);
        }
    }

    private void updateViewVisibility(final boolean progressing, String hintText) {
        progressStateView.setVisibility(progressing ? View.VISIBLE : View.GONE);
        btnSearch.setVisibility(progressing ? View.GONE : View.VISIBLE);
        rvFileList.setVisibility(progressing ? View.GONE : View.VISIBLE);
        if (progressing) {
            progressStateView.progressing(hintText);
        } else {
            progressStateView.complete(hintText);
        }
    }
}
