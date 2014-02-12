package com.ignite.boycott.io.model;

import com.android.internal.util.Predicate;

import java.util.Arrays;

/**
* Created by meseer on 12.02.14.
*/
public class Category {
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
}
