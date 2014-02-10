package com.ignite.boycott.loader;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.ignite.boycott.BlacklistedMaker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mdelegan on 10.02.14.
 */
public class BlacklistLoader extends android.support.v4.content.AsyncTaskLoader<Collection<BlacklistedMaker>> {
    public BlacklistLoader(Context context) {
        super(context);
    }

    @Override
    public Collection<BlacklistedMaker> loadInBackground() {
        try {
            HttpsURLConnection con = (HttpsURLConnection)new URL("https://script.google.com/macros/s/AKfycbxWbLBL6_7FJkb5fPj6PdyE45EoOCwgFVaAH6H0QmMcgiP8EVo6/exec?json=type3&zip=1").openConnection();
            BufferedInputStream stream = null;
            try {
                stream = new BufferedInputStream(new ZipInputStream(con.getInputStream()));
                return parseBlacklistJson(stream);
            } finally {
                if (stream != null) stream.close();
                con.disconnect();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            //TODO: Retry
            throw new RuntimeException(e);
        }
    }

    private Collection<BlacklistedMaker> parseBlacklistJson(BufferedInputStream stream) {
        Gson gson = new Gson();
        InputStreamReader is = new InputStreamReader(stream, Charset.forName("UTF-8"));
        try {
            Message m = gson.fromJson(is, Message.class);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.w("Exception when closing ISReader", e);
            }
        }
        return null;
    }

    private static class Message {
        private static Data data;
    }

    private static class Data {
        private int tableSize;
        private Category[] categories;
    }

    private static class Category {
        private String name;
        private int index;
        private Maker[] brand;
    }

    private static class Maker {
        private String brand;
        private String description;
        private String owner;
        private String reason;
        private String alternative;
        private String[] location;
        private String url;
        private String logoUrl;
    }
}
