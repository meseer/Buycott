package com.ignite.boycott.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.ignite.boycott.dao.model.BlacklistedMaker;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by meseer on 27.12.13.
 */
public class BlacklistDao extends SQLiteAssetHelper {
    private static final int version = 1;
    private static final String name = "blacklist";
    private static volatile BlacklistDao blacklistDao;
    private final SQLiteDatabase db;

    //TODO: Test database roll-out when not enough space on device
    private BlacklistDao(Context context) {
        super(context, name, null, version);

        db = getReadableDatabase();
    }

    public BlacklistedMaker getBlacklistedMaker(long blacklistId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("blacklist");
        String[] sqlSelect = {"Maker", "Type", "Owner", "Affiliation", "Alternative"};
        Cursor c = qb.query(db, sqlSelect,
                "_id = ?",
                new String[]{Long.toString(blacklistId)}, null, null, null);

        BlacklistedMaker maker = BlacklistedMaker.fromCursor(c);
        c.close();
        return maker;
    }

    public String getOwner(String maker) {
        Cursor c = db.rawQuery("select Owner from blacklist where Maker = ?",
                new String[]{maker});
        if (c.getCount() == 0) return null;

        c.moveToFirst();
        String owner = c.getString(0);
        c.close();
        return owner;
    }

    /**
     * Returns Loader&lt;Cursor&gt; which loads following 3 columns:
     *  <li>_id</li>
     *  <li>Maker</li>
     *  <li>Owner</li>
     *
     * @param context
     * @return
     */
    public static Loader<Cursor> newHistoryLoader(Context context, CharSequence constraint) {
        String sql = "select _id, Maker, Owner from blacklist";
        String args[] = null;
        if (!TextUtils.isEmpty(constraint)) {
            sql += " where Maker like ?";
            sql += " or Owner like ?";
            String likeClause = "%" + constraint + "%";
            args = new String[] {likeClause, likeClause};
        }

        return new SQLiteCursorLoader(context, instance(context), sql, args);
    }

    public static BlacklistDao instance(Context context) {
        if (blacklistDao == null) {
            synchronized (BlacklistDao.class) {
                if (blacklistDao == null)
                    blacklistDao = new BlacklistDao(context);
            }
        }
        return blacklistDao;
    }
}
