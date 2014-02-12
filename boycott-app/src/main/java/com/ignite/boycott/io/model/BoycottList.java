package com.ignite.boycott.io.model;

/**
* Created by meseer on 12.02.14.
*/
public class BoycottList {
    private Data Data;

    public Data getData() {
        return Data;
    }

    public Maker getItem(int position) {
        int i = 0;

        while (Data.getCategories()[i].getNodes().length <= position) {
            position-=Data.getCategories()[i].getNodes().length;
            i++;
        }
        return Data.getCategories()[i].getNodes()[position];
    }

    public int size() {
        int count = 0;

        for (Category c : Data.getCategories())
            count += c.getNodes().length;

        return count;
    }

    public long getPosition(Maker maker) {
        //TODO: Save position in the maker itself
        int position = 0;
        for (Category c : Data.getCategories()) {
            for (Maker m : c.getNodes()) {
                if (m == maker) return position;
                position++;
            }
        }
        throw new RuntimeException("Maker not found in the list");
    }

    public BoycottList filter(String mFilter) {
        BoycottList result = new BoycottList();
        result.Data = Data.filter(mFilter);
        return result;
    }
}
