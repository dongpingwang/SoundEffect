package com.flyaudio.soundeffect.backup.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.lib.utils.StorageUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.bean.Device;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-1-15
 * email  wangdongping@flyaudio.cn
 */
public final class UsbManager {

    private List<UsbListener> listeners = new ArrayList<>();
    private UsbReceiver usbReceiver;

    private UsbManager() {

    }

    public static UsbManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static UsbManager instance = new UsbManager();
    }

    public void init() {
        // 注册U盘广播监听
        usbReceiver = new UsbReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        AppUtils.getContext().registerReceiver(usbReceiver, intentFilter);
    }

    public void deInit() {
        AppUtils.getContext().unregisterReceiver(usbReceiver);
    }

    public interface UsbListener {
        /**
         * U盘挂载
         *
         * @param path U盘路径
         */
        void insert(String path);

        /**
         * U盘弹出
         *
         * @param path U盘路径
         */
        void eject(String path);
    }

    public void addUsbListener(@NonNull UsbListener listener) {
        this.listeners.add(listener);
    }

    public void removeUsbListener(@NonNull UsbListener listener) {
        this.listeners.remove(listener);
    }

    private final class UsbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(Intent.ACTION_MEDIA_MOUNTED, action)) {
                for (UsbListener listener : listeners) {
                    listener.insert(intent.getDataString());
                }
            } else if (TextUtils.equals(Intent.ACTION_MEDIA_EJECT, action)) {
                for (UsbListener listener : listeners) {
                    listener.eject(intent.getDataString());
                }
            }
        }
    }

    /**
     * 检查所有U盘、SD卡
     */
    public List<Device> checkAllDisk() {
        List<Device> devices = new ArrayList<>();
        List<StorageUtils.VolumeInfo> volumes = StorageUtils.getVolumes();
        for (StorageUtils.VolumeInfo volume : volumes) {
            String state = StorageUtils.VolumeInfo.getEnvironmentForState(volume.getState());
            StorageUtils.DiskInfo disk = volume.getDisk();
            if (TextUtils.equals(Environment.MEDIA_MOUNTED, state) && disk != null) {
                if (disk.isUsb() || disk.isSd()) {
                    File file = volume.getPath();
                    String path = file.getAbsolutePath();
                    String description = volume.getDescription();
                    if (TextUtils.isEmpty(description)) {
                        description = disk.isUsb() ? ResUtils.getString(R.string.usb) : ResUtils.getString(R.string.sdcard);
                    }
                    devices.add(new Device(description, path));
                }
            }
        }
        Device internalStorage = new Device();
        internalStorage.setDescription(ResUtils.getString(R.string.internal_storage));
        internalStorage.setPath(Environment.getExternalStorageDirectory().getAbsolutePath());
        devices.add(internalStorage);
        Device firstDevice = devices.get(0);
        firstDevice.setChecked(true);
        devices.set(0, firstDevice);
        return devices;
    }

}

