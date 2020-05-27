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

    @Override
    public String toString() {
        return "EqMode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }
}
