package com.ignite.boycott.reporting.crashlytics;

/**
 * Created by meseer on 08.02.14.
 */
public class WrongMakerException extends RuntimeException {
    public WrongMakerException(String barcode, String detectedMaker, String correctMaker) {
        super("Barcode " + barcode
                + " associated with Maker " + detectedMaker + ", but should be " + correctMaker);
    }
}
