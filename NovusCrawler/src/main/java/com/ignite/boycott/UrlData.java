package com.ignite.boycott;

/**
 * Created by mdelegan on 25.12.13.
 */
public class UrlData {
    public final String url;
    public final String barcode;
    public int countryCode;
    public int makerCode;

    public UrlData(String url, String barcode) {
        this.url = url;
        this.barcode = barcode;
        if (isProduct()) parseBarcode();
    }

    private void parseBarcode() {
        countryCode = Integer.valueOf(barcode.substring(0, 3));
        makerCode = Integer.valueOf(barcode.substring(3, 7));
    }

    public boolean isProduct() { return barcode != null; };
}
