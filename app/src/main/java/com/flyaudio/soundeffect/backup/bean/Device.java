package com.flyaudio.soundeffect.backup.bean;

/**
 * @author Dongping Wang
 * date 2020/5/23  22:23
 * email wangdongping@flyaudio.cn
 */
public class Device {

    public Device(String description, String path) {
        this.description = description;
        this.path = path;
    }

    public Device() {

    }

    private String description;
    private String path;
    private boolean checked;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
