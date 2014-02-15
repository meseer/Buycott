package com.ignite.boycott.io.model;

import com.android.internal.util.Predicate;

/**
* Created by meseer on 12.02.14.
*/
public class BoycottList {
    private Data Data;

    public Data getData() {
        return Data;
    }

    public Maker getMaker(int globalPosition) {
        int i = 0;

        while (Data.getCategories()[i].getNodes().length <= globalPosition) {
            globalPosition-=Data.getCategories()[i].getNodes().length;
            i++;
        }
        return Data.getCategories()[i].getNodes()[globalPosition];
    }

    public int size() {
        int count = 0;

        if (Data != null) {
            for (Category c : Data.getCategories())
                count += c.getNodes().length;
        }

        return count;
    }

    public long getPosition(Maker maker) {
        //TODO: Save position in the maker itself
        int position = 0;
        if (Data != null) {
            for (Category c : Data.getCategories())
                for (Maker m : c.getNodes()) {
                    if (m == maker) return position;
                    position++;
                }
        }
        throw new RuntimeException("Maker not found in the list");
    }

    public BoycottList filterMakers(Predicate<Maker> mFilter) {
        BoycottList result = new BoycottList();
        result.Data = Data.filterMakers(mFilter);
        return result;
    }

    public Category getCategory(int position) {
        if (position < Data.getCategories().length)
            return Data.getCategories()[position];

        throw new RuntimeException("Category index out of bounds");
    }
}
