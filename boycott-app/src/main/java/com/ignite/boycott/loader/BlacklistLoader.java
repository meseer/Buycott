package com.ignite.boycott.loader;

import android.content.Context;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

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

/**
 * Created by mdelegan on 10.02.14.
 */
public class BlacklistLoader extends android.support.v4.content.AsyncTaskLoader<BoycottList> {
    public static final String BOYCOTT_LIST_URL = "https://script.google.com/macros/s/AKfycbxWbLBL6_7FJkb5fPj6PdyE45EoOCwgFVaAH6H0QmMcgiP8EVo6/exec?json=type3&zip=1";
    private OkHttpClient client;
    private BoycottList mBoycottList;

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
        try {
            HttpURLConnection con = client.open(new URL(BOYCOTT_LIST_URL));

            InputStream cis = null;
            Base64InputStream bis = null;
            try {
                cis = con.getInputStream();
                bis = new Base64InputStream(cis, Base64.DEFAULT);
                InputStream jsonStream = getFirstZipEntryAsStream(new ZipInputStream(bis));
                try {
                    return parseBlacklistJson(jsonStream);
                } finally {
                    if (jsonStream != null) jsonStream.close();
                }
            } finally {
                if (bis != null) bis.close();
                if (cis != null) cis.close();
                con.disconnect();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            //TODO: Retry
            throw new RuntimeException(e);
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
                baos.close();
            }
        }
        throw new RuntimeException("Downloaded ZIP is empty");
    }

    public BoycottList parseBlacklistJson(InputStream stream) {
        Gson gson = new Gson();
        InputStreamReader is = new InputStreamReader(stream, Charset.forName("UTF-8"));
        try {
            BoycottList boycottList = gson.fromJson(is, BoycottList.class);
            return boycottList;
        } finally {
            try {
                is.close();
                stream.close();
            } catch (IOException e) {
                Log.w("Failed to close InputStreamReader", e);
            }
        }
    }
}
