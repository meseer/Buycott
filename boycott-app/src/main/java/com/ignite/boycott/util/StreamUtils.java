package com.ignite.boycott.util;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int count;
        while ((count = is.read(buffer)) != -1) {
            os.write(buffer, 0, count);
        }
    }
}
