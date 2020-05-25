package com.flyaudio.soundeffect.backup.logic;

import com.flyaudio.lib.async.AsyncWorker;
import com.flyaudio.soundeffect.backup.bean.Device;
import com.flyaudio.soundeffect.backup.bean.EffectFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-5-25
 * email wangdongping@flyaudio.cn
 */
public class ScanManager {

    private ScanManager() {

    }

    private static final class InstanceHolder {
        private static ScanManager instance = new ScanManager();
    }

    public static ScanManager getInstance() {
        return InstanceHolder.instance;
    }

    public void scanEffectFileAsync(AsyncWorker.ResultCallback<List<EffectFile>> callback) {
        AsyncWorker.execute(new AsyncWorker.ResultTask<List<EffectFile>>() {
            @Override
            public List<EffectFile> doInBackground() {
                List<Device> devices = UsbManager.getInstance().getAllDisk();
                List<EffectFile> result = new ArrayList<>();
                for (Device device : devices) {
                    result.addAll(ScanUtils.scanEffectFiles(new File(device.getPath())));
                }
                return result;
            }
        }, callback);
    }
}
