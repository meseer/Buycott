package com.ignite.boycott;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by meseer on 14.01.14.
 */
public class BlacklistedMaker implements Parcelable {
    public final String maker;
    public final String owner;
    public final String type;
    public final String affiliation;
    public final String alternative;

    @Override
    public String toString() {
        return "BlacklistedMaker{" +
                "maker='" + maker + '\'' +
                ", owner='" + owner + '\'' +
                ", type='" + type + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", alternative='" + alternative + '\'' +
                '}';
    }

    public BlacklistedMaker(String maker, String owner, String type, String affiliation, String alternative) {
        this.maker = maker;
        this.owner = owner;
        this.type = type;
        this.affiliation = affiliation;
        this.alternative = alternative;
    }

    public static BlacklistedMaker fromCursor(Cursor c) {
        if (c.getCount() == 0) return null;

        c.moveToFirst();
        int makerIdx = c.getColumnIndexOrThrow("Maker");
        int ownerIdx = c.getColumnIndexOrThrow("Owner");
        int typeIdx = c.getColumnIndexOrThrow("Type");
        int affiliationIdx = c.getColumnIndexOrThrow("Affiliation");
        int alternativeIdx = c.getColumnIndexOrThrow("Alternative");

        return new BlacklistedMaker(
                c.getString(makerIdx),
                c.getString(ownerIdx),
                c.getString(typeIdx),
                c.getString(affiliationIdx),
                c.getString(alternativeIdx)
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    BlacklistedMaker(Parcel in) {
        this.maker = in.readString();
        this.owner = in.readString();
        this.type = in.readString();
        this.affiliation = in.readString();
        this.alternative = in.readString();
    }

    static final Creator<BlacklistedMaker> CREATOR = new Creator<BlacklistedMaker>() {
        @Override
        public BlacklistedMaker createFromParcel(Parcel source) {
            return new BlacklistedMaker(source);
        }

        @Override
        public BlacklistedMaker[] newArray(int size) {
            return new BlacklistedMaker[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maker);
        dest.writeString(owner);
        dest.writeString(type);
        dest.writeString(affiliation);
        dest.writeString(alternative);
    }
}
