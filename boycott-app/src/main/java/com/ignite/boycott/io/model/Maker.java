package com.ignite.boycott.io.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
* Created by meseer on 12.02.14.
*/
public class Maker implements Parcelable {
    private String Brand;
    private String Description;
    private String Owner;
    private String Reason;
    private String Alternative;
    private String[] Location;
    private String URL;
    private String LogoURL;

    public String getBrand() {
        return Brand;
    }

    public String getDescription() {
        return Description;
    }

    public String getOwner() {
        return Owner;
    }

    public String getReason() {
        return Reason;
    }

    public String getAlternative() {
        return Alternative;
    }

    public String[] getLocation() {
        return Location;
    }

    public String getURL() {
        return URL;
    }

    public String getLogoURL() {
        return LogoURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Brand);
        dest.writeString(Description);
        dest.writeString(Owner);
        dest.writeString(Reason);
        dest.writeString(Alternative);
        dest.writeStringArray(Location);
        dest.writeString(URL);
        dest.writeString(LogoURL);
    }

    Maker(Parcel in) {
        this.Brand = in.readString();
        this.Description = in.readString();
        this.Owner = in.readString();
        this.Reason = in.readString();
        this.Alternative = in.readString();
        this.Location = in.createStringArray();
        this.URL = in.readString();
        this.LogoURL = in.readString();
    }

    public static final Creator<Maker> CREATOR = new Creator<Maker>() {
        @Override
        public Maker createFromParcel(Parcel source) {
            return new Maker(source);
        }

        @Override
        public Maker[] newArray(int size) {
            return new Maker[size];
        }
    };
}
