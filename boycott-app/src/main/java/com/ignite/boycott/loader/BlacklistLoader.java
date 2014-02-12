package com.ignite.boycott.loader;

import android.content.Context;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.ignite.boycott.io.model.BoycottList;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.ignite.boycott.util.StreamUtils.safeClose;

/**
 * Created by mdelegan on 10.02.14.
 */
public class BlacklistLoader extends android.support.v4.content.AsyncTaskLoader<BoycottList> {
    public static final URL BOYCOTT_LIST_URL;
    private OkHttpClient client;
    private BoycottList mBoycottList;

    static {
        try {
            BOYCOTT_LIST_URL = new URL("https://script.google.com/macros/s/AKfycbxWbLBL6_7FJkb5fPj6PdyE45EoOCwgFVaAH6H0QmMcgiP8EVo6/exec?json=type3&zip=1");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public BlacklistLoader(Context context) {
        super(context);
        client = new OkHttpClient();
        client.setReadTimeout(100, TimeUnit.SECONDS);
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(BoycottList boycottList) {
        mBoycottList = boycottList;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(boycottList);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mBoycottList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mBoycottList);
        }

        if (takeContentChanged() || mBoycottList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    //Use RoboSpice or BoltsFramework or Ion etc.
    public BoycottList loadInBackground() {
        InputStream jsonStream = null;
        try {
            jsonStream = openJsonStreamFromUrl(BOYCOTT_LIST_URL);
            return parseBlacklistJson(jsonStream);
        } catch (IOException e) {
            Crashlytics.logException(e);
            throw new RuntimeException(e);
        } finally {
            safeClose(jsonStream);
        }
    }

    private InputStream openJsonStreamFromUrl(URL boycottListUrl) throws IOException {
        HttpURLConnection con = client.open(boycottListUrl);

        InputStream cis = null;
        Base64InputStream bis = null;
        try {
            cis = con.getInputStream();
            bis = new Base64InputStream(cis, Base64.DEFAULT);
            return getFirstZipEntryAsStream(new ZipInputStream(bis));
        } finally {
            safeClose(bis);
            safeClose(cis);
            con.disconnect();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        mBoycottList = null;
    }

    private InputStream getFirstZipEntryAsStream(ZipInputStream zis) throws IOException {
        ZipEntry ze = zis.getNextEntry();
        if (ze != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            return new ByteArrayInputStream(baos.toByteArray());
        } finally {
            safeClose(baos);
            safeClose(zis);
        }
    }
        throw new RuntimeException("Downloaded ZIP is empty");
    }

    public BoycottList parseBlacklistJson(InputStream stream) {
        InputStreamReader is = new InputStreamReader(stream, Charset.forName("UTF-8"));
        try {
            return new Gson().fromJson(is, BoycottList.class);
        } finally {
            safeClose(is);
        }
    }
}
