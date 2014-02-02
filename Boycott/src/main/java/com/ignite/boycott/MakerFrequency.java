package com.ignite.boycott;

import android.os.Parcel;
import android.os.Parcelable;

/**
* Created by meseer on 02.02.14.
*/
public class MakerFrequency implements Parcelable {
    public final String maker;
    public final int totalProducts;

    public MakerFrequency(String maker, int totalProducts) {
        this.maker = maker;
        this.totalProducts = totalProducts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maker);
        dest.writeInt(totalProducts);
    }

    static final Creator<MakerFrequency> CREATOR = new Creator<MakerFrequency>() {
        @Override
        public MakerFrequency createFromParcel(Parcel source) {
            return new MakerFrequency(source.readString(), source.readInt());
        }

        @Override
        public MakerFrequency[] newArray(int size) {
            return new MakerFrequency[size];
        }
    };
}
