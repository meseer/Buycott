package com.ignite.boycott.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ignite.boycott.dao.model.MakerFrequency;
import com.ignite.boycott.dao.model.Product;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by meseer on 27.12.13.
 */
public class ProductsDao extends SQLiteAssetHelper {
    private static final int version = 2;
    private static final String name = "makers";
    private static volatile ProductsDao productsDao;
    private final SQLiteDatabase db;
    private final Context context;

    //TODO: Test database roll-out when not enough space on device
    private ProductsDao(Context context) {
        super(context, name, null, version);
        this.context = context;

        setForcedUpgrade(2);
        db = getReadableDatabase();
    }

    public static ProductsDao instance(Context context) {
        if (productsDao == null) {
            synchronized (ProductsDao.class) {
                if (productsDao == null)
                    productsDao = new ProductsDao(context);
            }
        }
        return productsDao;
    }

    private String getCountryCode(String barcode) {
        return barcode.substring(0, 3);
    }

    private String getMakerCode(String barcode) {
        return barcode.substring(3, 8);
    }

    public Product getProduct(String barcode) {
        Cursor c = db.rawQuery("select Barcode,Maker,Title,CountryCode from makers where barcode = ?", new String[]{barcode});

        Product product = Product.fromCursor(c);
        c.close();
        return product;
    }

    public ArrayList<MakerFrequency> getMakers(String barcode) {
        String sql = "SELECT Maker,\n" +
                "       count( Maker ) as \"Count\"\n" +
                "  FROM makers\n" +
                " WHERE CountryCode = ? \n" +
                "       AND\n" +
                "       MakerCode = ? \n" +
                "       AND\n" +
                "       Maker <> ''\n" +
                " GROUP BY Maker\n" +
                " ORDER BY count( Maker )  DESC;\n";
        String countryCode = getCountryCode(barcode);
        String makerCode = getMakerCode(barcode);

        Cursor c = db.rawQuery(sql, new String[]{countryCode, makerCode});

        ArrayList<MakerFrequency> result = new ArrayList<>();

        int makerId = c.getColumnIndexOrThrow("Maker");
        int countId = c.getColumnIndexOrThrow("Count");
        while (c.moveToNext()) {
            result.add(new MakerFrequency(c.getString(makerId), c.getInt(countId)));
        }
        c.close();
        return result;
    }
}
