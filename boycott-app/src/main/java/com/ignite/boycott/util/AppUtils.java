package com.ignite.boycott.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.ignite.boycott.BuildConfig;

/**
 * Created by meseer on 13.02.14.
 */
public class AppUtils {
    /**
     * Launch Crashlytics reporting for current thread if in Production mode
     * Set up Strict Mode in Debug mode
     * @param context
     */
    public static void startMonitoring(Context context) {
        if (BuildConfig.DEBUG) {
            setUpStrictMode();
        } else {
            Crashlytics.start(context);
        }
    }

    private static void setUpStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.VmPolicy.Builder vmPolicy = new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath();
        setUpStrictModeHoneycomb(vmPolicy);
        StrictMode.setVmPolicy(vmPolicy.build());
    }

    @TargetApi(11)
    private static void setUpStrictModeHoneycomb(StrictMode.VmPolicy.Builder vmPolicy) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            vmPolicy.detectLeakedClosableObjects();
        }
    }
}
