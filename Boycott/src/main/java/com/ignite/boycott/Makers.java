package com.ignite.boycott;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by meseer on 27.12.13.
 */
public class Makers extends SQLiteAssetHelper {
    private static final int version = 1;
    private static final String name = "makers";

    public Makers(Context context) {
        super(context, name, null, version);
    }

    public Cursor getProduct(String barcode) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("makers");
        String[] sqlSelect = {"CountryCode", "ProductCode", "Barcode as _id", "Maker", "Title"};
        Cursor c = qb.query(db, sqlSelect,
                "_id = " + barcode, null, null, null, null);

        return c;
    }
}
