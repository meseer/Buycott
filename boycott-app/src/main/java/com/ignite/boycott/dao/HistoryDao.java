package com.ignite.boycott.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.ignite.boycott.HistoryEntry;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by meseer on 27.12.13.
 */
public class HistoryDao extends SQLiteAssetHelper {
    private static final int version = 1;
    private static final String name = "history";
    private static volatile HistoryDao historyDao;
    private final SQLiteDatabase db;

    //TODO: Test database roll-out when not enough space on device
    private HistoryDao(Context context) {
        super(context, name, null, version);

        db = getWritableDatabase();
    }

    public static HistoryDao instance(Context context) {
        if (historyDao == null) {
            synchronized (BlacklistDao.class) {
                if (historyDao == null)
                    historyDao = new HistoryDao(context);
            }
        }
        return historyDao;
    }

    public static String[] LOADER_COLUMNS = new String[] {"_id", "Barcode", "Maker", "Owner",
            "ProductName", "WasBlacklisted"};

    public static Loader<Cursor> createLoader(Context context) {
        String sql = "select _id, Barcode, Maker, Owner, ProductName, WasBlacklisted from history order by _id desc";

        return new SQLiteCursorLoader(context, new HistoryDao(context), sql, null);
    }

    public long log(HistoryEntry entry) {
        return db.insert("history", null, entry.asContentValues());
    }

    public long log(String mBarcode) {
        return log(new HistoryEntry(null, mBarcode, null, null, null));
    }
}
