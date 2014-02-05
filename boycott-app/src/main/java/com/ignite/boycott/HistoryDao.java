package com.ignite.boycott;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by meseer on 27.12.13.
 */
public class HistoryDao extends SQLiteAssetHelper {
    private static final int version = 1;
    private static final String name = "history";
    private final Context context;

    //TODO: Test database roll-out when not enough space on device
    public HistoryDao(Context context) {
        super(context, name, null, version);
        this.context = context;
    }

    public static String[] LOADER_COLUMNS = new String[] {"_id", "Barcode", "Maker", "Owner",
            "ProductName", "WasBlacklisted"};

    public static Loader<Cursor> createLoader(FragmentActivity activity) {
        String sql = "select _id, Barcode, Maker, Owner, ProductName, WasBlacklisted from history";

        return new SQLiteCursorLoader(activity, new HistoryDao(activity), sql, null);
    }

    public long log(HistoryEntry entry) {
        SQLiteDatabase db = getWritableDatabase();

        long id = db.insert("history", null, entry.asContentValues());

        return id;
    }

    public void log(String mBarcode) {
        log(new HistoryEntry(null, mBarcode, null, null, null));
    }
}
