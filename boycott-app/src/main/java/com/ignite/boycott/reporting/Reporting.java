package com.ignite.boycott.reporting;

/**
 * Created by meseer on 08.02.14.
 */
public interface Reporting {
    void reportMakerNotFound(String barcode, String maker, String product, Boolean blacklisted);
    void reportShouldBeBanned(String barcode, String maker);
    void reportWrongMaker(String barcode, String detectedMaker, String correctMaker);
    void reportShouldNotBeBanned(String barcode, String maker);
    void reportWrongProduct(String barcode, String detectedProduct, String correctProduct);
}
