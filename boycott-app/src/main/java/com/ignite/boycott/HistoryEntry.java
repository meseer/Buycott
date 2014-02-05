package com.ignite.boycott;

/**
 * Created by mdelegan on 05.02.14.
 */
public class HistoryEntry {
    public final boolean wasBlacklisted;
    public final String barcode;
    public final String maker;
    public final String owner;
    public final String productName;

    public HistoryEntry(boolean wasBlacklisted, String barcode, String maker, String owner, String productName) {
        this.wasBlacklisted = wasBlacklisted;
        this.barcode = barcode;
        this.maker = maker;
        this.owner = owner;
        this.productName = productName;
    }
}
