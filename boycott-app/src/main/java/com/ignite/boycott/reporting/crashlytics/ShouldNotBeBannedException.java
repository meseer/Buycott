package com.ignite.boycott.reporting.crashlytics;

/**
 * Created by meseer on 08.02.14.
 */
public class ShouldNotBeBannedException extends RuntimeException {
    public ShouldNotBeBannedException(String barcode, String maker) {
        super("Barcode " + barcode
                + " associated with Maker " + maker + " should not be banned");
    }
}
