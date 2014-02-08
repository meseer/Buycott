package com.ignite.boycott.reporting.crashlytics;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.ignite.boycott.reporting.Reporting;

public class CrashlyticsReporting implements Reporting {
    public CrashlyticsReporting() {
    }

    @Override
    public void reportMakerNotFound(String barcode, String maker, String product, Boolean blacklisted) {
        report(new MakerNotFoundException(barcode, maker, product, blacklisted));
    }

    @Override
    public void reportShouldBeBanned(String barcode, String maker) {
        report(new ShouldBeBannedException(barcode, maker));
    }

    @Override
    public void reportWrongMaker(String barcode, String detectedMaker, String correctMaker) {
        report(new WrongMakerException(barcode, detectedMaker, correctMaker));
    }

    @Override
    public void reportShouldNotBeBanned(String barcode, String maker) {
        report(new ShouldNotBeBannedException(barcode, maker));
    }

    @Override
    public void reportWrongProduct(String barcode, String detectedProduct, String correctProduct) {
        report(new WrongProductException(barcode, detectedProduct, correctProduct));
    }

    private void report(Throwable throwable) {
        Crashlytics.logException(throwable);
        Crashlytics.log(Log.INFO, "boycott-wrong-maker", throwable.getMessage());
    }
}