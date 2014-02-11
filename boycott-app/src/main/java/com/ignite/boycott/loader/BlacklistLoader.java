package com.ignite.boycott.loader;

import android.content.Context;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import com.google.gson.Gson;
import com.ignite.boycott.BlacklistedMaker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.zip.ZipEntry;
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
    public Collection<BlacklistedMaker> loadInBackground() {
        try {
            HttpsURLConnection con = (HttpsURLConnection)new URL("https://script.google.com/macros/s/AKfycbxWbLBL6_7FJkb5fPj6PdyE45EoOCwgFVaAH6H0QmMcgiP8EVo6/exec?json=type3&zip=1").openConnection();
            con.setReadTimeout(100000);
            InputStream cis = null;
            Base64InputStream bis = null;
            ZipInputStream zis = null;
            try {
                cis = con.getInputStream();
                bis = new Base64InputStream(cis, Base64.DEFAULT);
                zis = new ZipInputStream(bis);
                ZipEntry ze = zis.getNextEntry();
                if (ze != null) {
                    //close baos
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                    }
                    //close bais
                    return parseBlacklistJson(new ByteArrayInputStream(baos.toByteArray()));
                }
                throw new RuntimeException("Empty stream");
            } finally {
                if (zis != null) zis.close();
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

    public Collection<BlacklistedMaker> parseBlacklistJson(InputStream stream) {
        Gson gson = new Gson();
        InputStreamReader is = new InputStreamReader(stream, Charset.forName("UTF-8"));
        try {
            Message m = gson.fromJson(is, Message.class);
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.w("Failed to close InputStreamReader", e);
            }
        }
    }

    private static class Message {
        private Data Data;
    }

    private static class Data {
        private int TableSize;
        private Category[] Categories;

        public int getTableSize() {
            return TableSize;
        }

        public Category[] getCategories() {
            return Categories;
        }
    }

    private static class Category {
        private String Title;
        private int Index;
        private Maker[] Nodes;

        public void setTitle(String title) {
            this.Title = title;
        }

        public void setIndex(int index) {
            this.Index = index;
        }

        public void setNodes(Maker[] nodes) {
            this.Nodes = nodes;
        }
    }

    private static class Maker {
        private String Brand;
        private String Description;
        private String Owner;
        private String Reason;
        private String Alternative;
        private String[] Location;
        private String URL;
        private String LogoURL;

        public String getBrand() {
            return Brand;
        }

        public String getDescription() {
            return Description;
        }

        public String getOwner() {
            return Owner;
        }

        public String getReason() {
            return Reason;
        }

        public String getAlternative() {
            return Alternative;
        }

        public String[] getLocation() {
            return Location;
        }

        public String getURL() {
            return URL;
        }

        public String getLogoURL() {
            return LogoURL;
        }
    }
}
