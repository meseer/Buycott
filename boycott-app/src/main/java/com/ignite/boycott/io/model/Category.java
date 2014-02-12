package com.ignite.boycott.io.model;

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
}
