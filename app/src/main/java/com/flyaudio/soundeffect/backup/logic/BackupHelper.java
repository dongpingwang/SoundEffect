package com.flyaudio.soundeffect.backup.logic;

import android.util.Xml;

import com.flyaudio.lib.log.Logger;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * @author Dongping Wang
 * @date 19-8-1
 * email wangdongping@flyaudio.cn
 */
final class BackupHelper {

    private static final String TAG = "BackupHelper";
    public static final String FILE_SUFFIX = ".snd";

    private BackupHelper() {

    }

    static boolean backup(String dir, String fileName) {
        File targetFile = new File(dir, fileName + FILE_SUFFIX);
        Logger.d(TAG, "备份音效: 路径 = " + dir + " 文件 = " + targetFile.getAbsolutePath());
        boolean success = false;
        try {
            Map<String, ?> data = SPCacheHelper.getInstance().getAll();
            if (!data.isEmpty()) {
                FileOutputStream fos = new FileOutputStream(targetFile);
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(fos, "UTF-8");
                serializer.startDocument("UTF-8", true);
                serializer.startTag(null, AppUtils.getPackageName());
                for (Map.Entry<String, ?> entry : data.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    Class<?> valueClass = value.getClass();
                    serializer.startTag(null, key);
                    serializer.attribute(null, "type", valueClass.getSimpleName());
                    serializer.text(value.toString());
                    serializer.endTag(null, entry.getKey());
                }
                serializer.endTag(null, AppUtils.getPackageName());
                serializer.endDocument();
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    static boolean restore(File sourceFile) {
        boolean success = false;
        SPCacheHelper spHelper = SPCacheHelper.getInstance();
        // TODO 注意导入失败的情况
        spHelper.clear();
        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                String key = parser.getName();
                if (type == XmlPullParser.START_TAG) {
                    if (!AppUtils.getPackageName().equals(key)) {
                        String classType = parser.getAttributeValue(null, "type");
                        String value;
                        value = parser.nextText();
                        if ("String".equals(classType)) {
                            spHelper.putString(key, value);
                        } else if ("Integer".equals(classType)) {
                            spHelper.putInt(key, Integer.parseInt(value));
                        } else if ("Boolean".equals(classType)) {
                            spHelper.putBoolean(key, Boolean.parseBoolean(value));
                        } else if ("Float".equals(classType)) {
                            spHelper.putFloat(key, Float.parseFloat(value));
                        } else if ("Long".equals(classType)) {
                            spHelper.putLong(key, Long.parseLong(value));
                        }
                    }
                }
                type = parser.next();
            }
            success = true;
            // 恢复数据后选择第一个Eq模式
            EqManager.getInstance().saveCurrentEq(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

}