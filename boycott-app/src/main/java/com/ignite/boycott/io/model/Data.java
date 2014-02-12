package com.ignite.boycott.io.model;

/**
* Created by meseer on 12.02.14.
*/
public class Data {
    private int TableSize;
    private Category[] Categories;

    public int getTableSize() {
        return TableSize;
    }

    public Category[] getCategories() {
        return Categories;
    }

    public Data filter(String filter) {
        Data result = new Data();
        result.Categories = new Category[Categories.length];
        for (int i = 0; i < Categories.length; i++)
            result.Categories[i] = Categories[i].filter(filter);

        return result;
    }
}
