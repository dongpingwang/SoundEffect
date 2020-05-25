package com.flyaudio.soundeffect.backup.logic;

import com.flyaudio.lib.async.AsyncWorker;

import java.io.File;

/**
 * @author Dongping Wang
 * @date 20-5-25
 * email wangdongping@flyaudio.cn
 */
public class BackupManager {

    private BackupManager() {

    }

    private static final class InstanceHolder {
        private static BackupManager instance = new BackupManager();
    }

    public static BackupManager getInstance() {
        return InstanceHolder.instance;
    }

    public void backupAsync(final String dir, final String name, AsyncWorker.ResultCallback<Boolean> callback) {
        AsyncWorker.execute(new AsyncWorker.ResultTask<Boolean>() {
            @Override
            public Boolean doInBackground() {
                return BackupHelper.backup(dir, name);
            }
        }, callback);
    }

    public void restoreAsync(final File sourceFile, AsyncWorker.ResultCallback<Boolean> callback) {
        AsyncWorker.execute(new AsyncWorker.ResultTask<Boolean>() {
            @Override
            public Boolean doInBackground() {
                return BackupHelper.restore(sourceFile);
            }
        }, callback);
    }
}
