package com.ignite.boycott.dao;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by meseer on 09.02.14.
 */
@Module
public class DatabaseModule {
    @Provides
    public BlacklistDao provideBlacklistDao(Context context) {
        return new BlacklistDao(context);
    }

    @Provides
    public HistoryDao provideHistoryDao(Context context) {
        return new HistoryDao(context);
    }
}
