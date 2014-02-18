package com.ignite.boycott.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.internal.util.Predicate;

/**
* Created by meseer on 12.02.14.
*/
public class Data implements Parcelable {
    private int TableSize;
    private Category[] Categories;

    public int getTableSize() {
        return TableSize;
    }

    public Category[] getCategories() {
        return Categories;
    }

    public Data filterMakers(Predicate<Maker> filter) {
        Data result = new Data();
        result.Categories = new Category[Categories.length];
        for (int i = 0; i < Categories.length; i++) {
            result.Categories[i] = Categories[i].filterMakers(filter);
        }

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(TableSize);
        dest.writeTypedArray(Categories, 0);
    }

    public Data() {
    }

    Data(Parcel in) {
        this.TableSize = in.readInt();
        this.Categories = in.createTypedArray(Category.CREATOR);
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
