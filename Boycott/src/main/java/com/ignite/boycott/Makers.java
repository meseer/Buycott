package com.ignite.boycott;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.ignite.boycott.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.List;

/**
 * Created by meseer on 27.12.13.
 */
public class Makers extends SQLiteAssetHelper {
    private static final int version = 1;
    private static final String name = "makers";
    private final Context context;

    //TODO: Test database roll-out when not enough space on device
    public Makers(Context context) {
        super(context, name, null, version);
        this.context = context;
    }

    @Deprecated
    public Cursor getProductOld(String barcode) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("makers m left outer join blacklist b on m.Maker = b.Maker");
        String[] sqlSelect = {"Barcode as _id", "m.Maker as Maker", "Title", "Type", "Owner", "Affiliation"};
        Cursor c = qb.query(db, sqlSelect,
                "Barcode = ?", new String[] {barcode}, null, null, null);

        return c;
    }

    public Cursor getMaker(String code) {
        String countryCode = getCountryCode(code);
        String makerCode = getMakerCode(code);

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT Barcode AS _id,\n" +
                "       m.Maker AS Maker,\n" +
                "       'N/A' AS Title,\n" +
                "       Type,\n" +
                "       Owner,\n" +
                "       Affiliation\n" +
                "  FROM makers m\n" +
                "       LEFT OUTER JOIN blacklist b\n" +
                "                    ON m.Maker = b.Maker\n" +
                "WHERE MakerCode = ? \n" +
                "       AND\n" +
                "       m.Maker <> '' \n" +
                "       AND\n" +
                "       CountryCode = ?\n" +
                "GROUP BY m.Maker\n" +
                "HAVING length( Barcode ) = 13\n" +
                "ORDER BY count(m.Maker) DESC;\n";

        return db.rawQuery(sql, new String[] {makerCode, countryCode});
    }

    public Cursor getProductOrMakers(String barcode) {
        Cursor c = getProductOld(barcode);
        if (c.getCount() == 0 && barcode.length() == 13) {
            c = getMaker(barcode);
        }
        return c;
    }

    public Cursor getProduct(String barcode) {
        if (barcode != null && barcode.length() == 13) {
            String countryCode = getCountryCode(barcode);
            String makerCode = getMakerCode(barcode);

            String sql = "SELECT r.Barcode as _id,\n" +
                    "       r.Maker as Maker,\n" +
                    "       r.Title as Title,\n" +
                    "       r.AltMaker as AltMaker,\n" +
                    "       b.Type as Type,\n" +
                    "       b.Owner as Owner,\n" +
                    "       b.Affiliation as Affiliation,\n" +
                    "       b.Alternative as Alternative\n" +
                    "  FROM ( \n" +
                    "    SELECT m.Barcode,\n" +
                    "           m.Maker,\n" +
                    "           m.Title,\n" +
                    "           AltMaker\n" +
                    "      FROM ( \n" +
                    "            SELECT DISTINCT Maker AS AltMaker\n" +
                    "                       FROM makers\n" +
                    "                      WHERE CountryCode = ? \n" +
                    "                            AND\n" +
                    "                            MakerCode = ? \n" +
                    "                            AND\n" +
                    "                            Maker <> '' \n" +
                    "        ) \n" +
                    "    \n" +
                    "           LEFT JOIN ( \n" +
                    "            SELECT *\n" +
                    "              FROM makers\n" +
                    "             WHERE Barcode = ? \n" +
                    "        ) \n" +
                    "        AS m \n" +
                    ") \n" +
                    "AS r\n" +
                    "       LEFT JOIN blacklist b\n" +
                    "              ON b.Maker = r.AltMaker\n" +
                    " ORDER BY b.Owner DESC\n" +
                    " LIMIT 1;\n";


            SQLiteDatabase db = getReadableDatabase();

            return db.rawQuery(sql,
                    new String[]{countryCode, makerCode, barcode == null ? "" : barcode});
        }

        return null;
    }

    private String getCountryCode(String barcode) {
        return barcode.substring(0, 3);
    }

    private String getMakerCode(String barcode) {
        return barcode.substring(3, 8);
    }

    public BlacklistedMaker getBlacklistedMaker(long blacklistId) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("blacklist");
        String[] sqlSelect = {"Maker", "Type", "Owner", "Affiliation", "Alternative"};
        Cursor c = qb.query(db, sqlSelect,
                "_id = ?",
                new String[] { Long.toString(blacklistId) }, null, null, null);

        return BlacklistedMaker.fromCursor(c);

    }

    public boolean containsProduct(String mBarcode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select 1 from makers where barcode = ?", new String[] {mBarcode});

        return c.getCount() > 0;
    }

    public boolean containsMaker(String mBarcode) {
        String sql = "select 1 from makers where CountryCode = ? and MakerCode = ? and Maker <> ''";
        String countryCode = getCountryCode(mBarcode);
        String makerCode = getMakerCode(mBarcode);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{countryCode, makerCode});

        return c.getCount() > 0;
    }
}
