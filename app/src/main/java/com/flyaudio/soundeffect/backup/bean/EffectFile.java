package com.flyaudio.soundeffect.backup.bean;

import com.flyaudio.lib.utils.FileUtils;

/**
 * @author Dongping Wang
 * @date 20-5-25
 * email wangdongping@flyaudio.cn
 */
public class EffectFile {

    private String path;
    private boolean checked;

    public EffectFile(String path) {
        this.path = path;
    }

    public String getName() {
        return FileUtils.getName(path, true);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
