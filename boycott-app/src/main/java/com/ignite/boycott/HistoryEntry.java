package com.ignite.boycott;

import android.content.ContentValues;

/**
 * Created by mdelegan on 05.02.14.
 */
public class HistoryEntry {
    public final Boolean wasBlacklisted;
    public final String barcode;
    public final String maker;
    public final String owner;
    public final String productName;

    public HistoryEntry(Boolean wasBlacklisted, String barcode, String maker, String owner, String productName) {
        this.wasBlacklisted = wasBlacklisted;
        this.barcode = barcode;
        this.maker = maker;
        this.owner = owner;
        this.productName = productName;
    }

    public ContentValues asContentValues() {
        ContentValues r = new ContentValues(5);
        r.put("WasBlacklisted", wasBlacklisted);
        r.put("Barcode", barcode);
        r.put("Maker", maker);
        r.put("Owner", owner);
        r.put("ProductName", productName);
        return r;
    }
}
