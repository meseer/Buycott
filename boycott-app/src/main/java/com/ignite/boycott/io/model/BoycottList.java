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
}
