package com.ignite.boycott;

import android.database.Cursor;

/**
 * Created by meseer on 14.01.14.
 */
public class BlacklistedMaker {
    public final String maker;
    public final String owner;
    public final String type;
    public final String affiliation;
    public final String alternative;

    @Override
    public String toString() {
        return "BlacklistedMaker{" +
                "maker='" + maker + '\'' +
                ", owner='" + owner + '\'' +
                ", type='" + type + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", alternative='" + alternative + '\'' +
                '}';
    }

    public BlacklistedMaker(String maker, String owner, String type, String affiliation, String alternative) {
        this.maker = maker;
        this.owner = owner;
        this.type = type;
        this.affiliation = affiliation;
        this.alternative = alternative;
    }

    public static BlacklistedMaker fromCursor(Cursor c) {
        if (c.getCount() == 0) return null;

        c.moveToFirst();
        int makerIdx = c.getColumnIndex("Maker");
        int ownerIdx = c.getColumnIndex("Owner");
        int typeIdx = c.getColumnIndex("Type");
        int affiliationIdx = c.getColumnIndex("Affiliation");
        int alternativeIdx = c.getColumnIndex("Alternative");

        if (makerIdx == -1 || ownerIdx == -1 || typeIdx == -1 || affiliationIdx == -1 ||
                alternativeIdx == -1) {
            throw new IllegalArgumentException("Cursor doesn't some of the required fields: " + c);
        }

        return new BlacklistedMaker(
                c.getString(makerIdx),
                c.getString(ownerIdx),
                c.getString(typeIdx),
                c.getString(affiliationIdx),
                c.getString(alternativeIdx)
        );
    }
}
