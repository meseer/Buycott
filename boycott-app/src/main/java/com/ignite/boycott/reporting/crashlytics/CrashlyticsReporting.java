package com.ignite.boycott.reporting.crashlytics;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.ignite.boycott.reporting.Reporting;

public class CrashlyticsReporting implements Reporting {
    public CrashlyticsReporting() {
    }

    @Override
    public void reportMakerNotFound(String barcode, String maker, String product, Boolean blacklisted) {
        Crashlytics.log(Log.INFO, "boycott-maker-not-found", "Barcode " + barcode
                + ", Maker " + maker + ", Product " + product
                + ", blacklisted=" + blacklisted);
        Crashlytics.logException(new MakerNotFoundException(barcode, maker, product, blacklisted));
    }

    @Override
    public void reportShouldBeBanned(String barcode, String maker) {
        Crashlytics.log(Log.INFO, "boycott-should-be-banned", "Barcode " + barcode
                + " associated with Maker " + maker + " should be banned");
        Crashlytics.logException(new ShouldBeBannedException(barcode, maker));
    }

    @Override
    public void reportWrongMaker(String barcode, String detectedMaker, String correctMaker) {

    }

    @Override
    public void reportShouldNotBeBanned(String barcode, String maker) {

    }

    @Override
    public void reportWrongProduct(String barcode, String detectedProduct, String correctProduct) {

    }
}