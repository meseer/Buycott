package com.ignite.boycott.util;

import android.util.Log;

import java.io.Closeable;

/**
 * Created by mdelegan on 12.02.14.
 */
public class StreamUtils {
    public static void safeClose(Closeable jsonStream) {
        if (jsonStream != null) {
            try {
                jsonStream.close();
            } catch (Exception e) {
                Log.w("Exception while closing InputStream", e);
            }
        }
    }
}
