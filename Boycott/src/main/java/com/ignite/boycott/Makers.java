package com.ignite.boycott;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import com.ignite.buycott.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.List;

/**
 * Created by meseer on 27.12.13.
 */
public class Makers extends SQLiteAssetHelper {
    private static final int version = 1;
    private static final String name = "makers";
    private final Context context;

    public Makers(Context context) {
        super(context, name, null, version);
        this.context = context;
    }

    public Cursor getProduct(String barcode) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("makers m left outer join blacklist b on m.Maker = b.Maker");
        String[] sqlSelect = {"Barcode as _id", "m.Maker as Maker", "Title", "Type", "Owner", "Affiliation"};
        Cursor c = qb.query(db, sqlSelect,
                "Barcode = ?", new String[] {barcode}, null, null, null);

        return c;
    }

    public Cursor getMaker(String code) {
        Integer countryCode = Integer.valueOf(code.substring(0,3));
        Integer makerCode = Integer.valueOf(code.substring(3, 8));

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("makers m left outer join blacklist b on m.Maker = b.Maker");
        String[] sqlSelect = {"Barcode as _id", "m.Maker as Maker", "'" + context.getString(R.string.n_a) + "' as Title",
                              "Type", "Owner", "Affiliation"};
        Cursor c = qb.query(db, sqlSelect,
                "MakerCode = ? and m.Maker <> '' and CountryCode = ?",
                new String[] { Integer.toString(makerCode), Integer.toString(countryCode) },
                "m.Maker", "length(Barcode) = 13", "Owner desc");

        return c;
    }

    public Cursor getProductOrMakers(String barcode) {
        Cursor c = getProduct(barcode);
        if (c.getCount() == 0 && barcode.length() == 13) {
            c = getMaker(barcode);
        }
        return c;
    }
}
