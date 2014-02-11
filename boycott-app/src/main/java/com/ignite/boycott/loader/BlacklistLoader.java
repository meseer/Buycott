package com.ignite.boycott.loader;

import android.content.Context;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.ignite.boycott.BlacklistedMaker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    //Use RoboSpice or OkHttp
    @Override
    public Collection<BlacklistedMaker> loadInBackground() {
        try {
//            HttpsURLConnection con = (HttpsURLConnection)new URL("https://script.google.com/macros/s/AKfycbxWbLBL6_7FJkb5fPj6PdyE45EoOCwgFVaAH6H0QmMcgiP8EVo6/exec?json=type3&zip=1").openConnection();
            HttpsURLConnection con = (HttpsURLConnection)new URL("https://script.google.com/macros/s/AKfycbxWbLBL6_7FJkb5fPj6PdyE45EoOCwgFVaAH6H0QmMcgiP8EVo6/exec?json=type3").openConnection();
            con.setReadTimeout(100000);
            InputStream cis = null;
//            Base64InputStream bis = null;
//            ZipInputStream zis = null;
            try {
                cis = con.getInputStream();
//                bis = new Base64InputStream(cis, Base64.DEFAULT);
//                zis = new ZipInputStream(bis);
                return parseBlacklistJson(cis);
            } finally {
//                if (zis != null) zis.close();
//                if (bis != null) bis.close();
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

    private Collection<BlacklistedMaker> parseBlacklistJson(InputStream stream) {
        Gson gson = new Gson();
        InputStreamReader is = new InputStreamReader(stream, Charset.forName("UTF-8"));
        try {
            Data m = gson.fromJson(is, Data.class);
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.w("Exception when closing ISReader", e);
            }
        }
    }

    private static class Message {
        private static Data data;
    }

    private static class Data {
        private int tableSize;
        private Category[] categories;

        public int getTableSize() {
            return tableSize;
        }

        public Category[] getCategories() {
            return categories;
        }
    }

    private static class Category {
        private String name;
        private int index;
        private Maker[] brand;

        public void setName(String name) {
            this.name = name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setBrand(Maker[] brand) {
            this.brand = brand;
        }
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

        public String getBrand() {
            return brand;
        }

        public String getDescription() {
            return description;
        }

        public String getOwner() {
            return owner;
        }

        public String getReason() {
            return reason;
        }

        public String getAlternative() {
            return alternative;
        }

        public String[] getLocation() {
            return location;
        }

        public String getUrl() {
            return url;
        }

        public String getLogoUrl() {
            return logoUrl;
        }
    }
}
