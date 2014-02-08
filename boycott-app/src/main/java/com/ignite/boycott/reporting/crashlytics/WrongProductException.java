package com.ignite.boycott.reporting.crashlytics;

/**
 * Created by meseer on 08.02.14.
 */
public class WrongProductException extends RuntimeException {
    public WrongProductException(String barcode, String detectedProduct, String correctProduct) {
        super("Barcode " + barcode
                + " associated with Product " + detectedProduct + ", but should be " + correctProduct);
    }
}
