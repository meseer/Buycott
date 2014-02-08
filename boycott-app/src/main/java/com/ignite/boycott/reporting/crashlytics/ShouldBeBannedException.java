package com.ignite.boycott.reporting.crashlytics;

/**
 * Created by meseer on 08.02.14.
 */
public class ShouldBeBannedException extends RuntimeException {
    public ShouldBeBannedException(String barcode, String maker) {
        super("Barcode " + barcode
                + " associated with Maker " + maker + " should be banned");
    }
}
