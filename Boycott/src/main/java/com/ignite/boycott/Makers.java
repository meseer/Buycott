package com.ignite.boycott;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.ignite.buycott.R;
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

    public Cursor getBlacklisted(String maker) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("blacklist");
        String[] sqlSelect = {"_id", "Maker", "Type", "Owner", "Affiliation", "Alternative"};
        Cursor c = qb.query(db, sqlSelect,
                "Maker = '" + maker + "'", null, null, null, null);

        return c;
    }

    public Cursor getProduct(String barcode) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("makers");
        String[] sqlSelect = {"MakerCode", "Barcode as _id", "Maker", "Title"};
        Cursor c = qb.query(db, sqlSelect,
                "_id = " + barcode, null, null, null, null);

        return c;
    }

    public Cursor getMaker(String code) {
        Integer countryCode = Integer.valueOf(code.substring(0,3));
        Integer makerCode = Integer.valueOf(code.substring(3, 8));

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("makers");
        String[] sqlSelect = {"MakerCode", "Barcode as _id", "Maker", "'" + R.string.n_a + "' as Title"};
        Cursor c = qb.query(db, sqlSelect,
                "MakerCode = " + makerCode + " and Maker <> '' and CountryCode = " + countryCode, null, null, null, null, "1");

        return c;
    }
}
