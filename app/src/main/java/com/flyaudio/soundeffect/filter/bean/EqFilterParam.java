package com.flyaudio.soundeffect.filter.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dongping Wang
 * @date 2020/5/26 20:22
 * email wangdongping@flyaudio.cn
 */
public class EqFilterParam implements Parcelable {

    public int channel = -1;
    public int freq;
    public int slope;
    public boolean enable;

    public EqFilterParam() {

    }

    protected EqFilterParam(Parcel in) {
        channel = in.readInt();
        freq = in.readInt();
        slope = in.readInt();
        enable = in.readByte() != 0;
    }

    public static final Creator<EqFilterParam> CREATOR = new Creator<EqFilterParam>() {
        @Override
        public EqFilterParam createFromParcel(Parcel in) {
            return new EqFilterParam(in);
        }

        @Override
        public EqFilterParam[] newArray(int size) {
            return new EqFilterParam[size];
        }
    };

    @Override
    public String toString() {
        return "EqFilterParam{" +
                "channel = " + channel +
                ", freq = " + freq +
                ", slope = " + slope +
                ", enable = " + enable +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(channel);
        dest.writeInt(freq);
        dest.writeInt(slope);
        dest.writeByte((byte) (enable ? 1 : 0));
    }
}

