package com.ignite.boycott;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by meseer on 01.02.14.
 */
public class Product implements Parcelable {
    public final String mBarcode;
    public final String mTitle;
    public final String mMaker;
    public final int mCountryCode;

    public Product(String barcode, String title, String maker, int countryCode) {
        this.mBarcode = barcode;
        this.mTitle = title;
        this.mMaker = maker;
        this.mCountryCode = countryCode;
    }

    Product(Parcel source) {
        this(source.readString(),
             source.readString(),
             source.readString(),
             source.readInt());
    }

    public static Product fromCursor(Cursor c) {
        if (c.getCount() == 0) return null;

        c.moveToFirst();
        int barcodeId = c.getColumnIndexOrThrow("Barcode");
        int titleId = c.getColumnIndexOrThrow("Title");
        int makerId = c.getColumnIndexOrThrow("Maker");
        int countryCode = c.getColumnIndexOrThrow("CountryCode");

        return new Product(c.getString(barcodeId),
                c.getString(titleId),
                c.getString(makerId),
                c.getInt(countryCode));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBarcode);
        dest.writeString(mTitle);
        dest.writeString(mMaker);
        dest.writeInt(mCountryCode);
    }

    static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
