package com.flyaudio.soundeffect.equalizer.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public class EqMode implements Parcelable {

    private int id;
    private String name;
    private boolean checked;

    public EqMode() {
        // default construction
    }

    public EqMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private EqMode(Parcel in) {
        id = in.readInt();
        name = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<EqMode> CREATOR = new Creator<EqMode>() {
        @Override
        public EqMode createFromParcel(Parcel in) {
            return new EqMode(in);
        }

        @Override
        public EqMode[] newArray(int size) {
            return new EqMode[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByte((byte) (checked ? 1 : 0));
    }
}
