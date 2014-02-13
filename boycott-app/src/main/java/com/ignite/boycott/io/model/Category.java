package com.ignite.boycott.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.internal.util.Predicate;

import java.util.Arrays;

/**
* Created by meseer on 12.02.14.
*/
public class Category implements Parcelable {
    private String Title;
    private int Index;
    private Maker[] Nodes;

    public String getTitle() {
        return Title;
    }

    public int getIndex() {
        return Index;
    }

    public Maker[] getNodes() {
        return Nodes;
    }

    public Category filterMakers(Predicate<Maker> filter) {
        Category result = new Category();
        result.Title = Title;
        result.Index = Index;

        Maker[] filteredMakers = new Maker[Nodes.length];
        int i = 0;
        for (Maker m : Nodes) {
            if (filter.apply(m)) {
                filteredMakers[i] = m;
                i++;
            }
        }
        result.Nodes = Arrays.copyOf(filteredMakers, i);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Category() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Index);
        dest.writeString(Title);
        dest.writeTypedArray(Nodes, 0);
    }

    Category(Parcel in) {
        this.Index = in.readInt();
        this.Title = in.readString();
        this.Nodes = in.createTypedArray(Maker.CREATOR);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
