package com.ignite.boycott.reporting.crashlytics;

/**
 * Created by meseer on 08.02.14.
 */
public class ShouldBeBannedException extends Throwable {
    private final String maker;
    private final String barcode;

    public ShouldBeBannedException(String barcode, String maker) {
        this.barcode = barcode;
        this.maker = maker;
    }

    @Override
    public String getMessage() {
        return "Barcode " + barcode
                + " associated with Maker " + maker + " should be banned";
    }
}
