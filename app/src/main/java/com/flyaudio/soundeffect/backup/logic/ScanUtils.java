package com.flyaudio.soundeffect.backup.logic;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.utils.FileUtils;
import com.flyaudio.soundeffect.backup.bean.EffectFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Dongping Wang
 * @date 20-2-3下午6:19
 * email wangdongping@flyaudio.cn
 */
public final class ScanUtils {

    private static final String TAG = "ScanUtils";

    /**
     * 扫描某个目录下的音频文件
     *
     * @param directory 需要扫描的根目录
     */
    public static synchronized List<EffectFile> scanEffectFiles(@NonNull File directory) {
        List<EffectFile> result = new ArrayList<>();
        ArrayDeque<File> queue = new ArrayDeque<>();
        // 初始化过滤器
        // 把要检索的文件夹路径放入队列中
        queue.offer(directory);
        while (!queue.isEmpty()) {
            // 把队首作为当前检索的路径
            directory = queue.poll();
            // 获得当前检索的路径所有的子文件和文件夹
            File[] files = directory.listFiles(audioFileFilter);
            if (files == null) {
                continue;
            }
            for (File file : files) {
                if (file.isFile()) {
                    Logger.d(TAG, "scanEffectFiles: directory = " + directory.getPath() + "  file = " + file.getAbsolutePath());
                    EffectFile effectFile = new EffectFile(file.getAbsolutePath());
                    result.add(effectFile);
                } else {
                    // 是文件夹入queue作为新的需要检索文件夹的路径
                    queue.offer(file);
                }
            }
        }
        return result;
    }

    private static FileFilter audioFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            String ex = "." + FileUtils.getFileExtension(file).toLowerCase(Locale.getDefault());
            return FileUtils.isDirectory(file) || TextUtils.equals(ex, BackupHelper.FILE_SUFFIX);
        }
    };

}
