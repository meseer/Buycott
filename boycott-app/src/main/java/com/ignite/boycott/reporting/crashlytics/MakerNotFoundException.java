package com.ignite.boycott.reporting.crashlytics;

import android.text.TextUtils;

/**
* Created by meseer on 16.01.14.
*/
class MakerNotFoundException extends RuntimeException {
    private final String barcode;
    private final String product;
    private final String maker;
    private final Boolean blacklisted;

    public MakerNotFoundException(String barcode, String maker, String product, Boolean blacklisted) {
        this.barcode = barcode;
        this.maker = maker;
        this.product = product;
        this.blacklisted = blacklisted;
    }

    @Override
    public String getMessage() {
        StringBuilder b = new StringBuilder("Maker not found for barcode ");
        b.append(barcode);
        if (!TextUtils.isEmpty(maker)) {
            b.append(", made by '").append(maker).append("'");
        }
        if (!TextUtils.isEmpty(product)) {
            b.append(". Repotedly product name is '").append(product).append("'");
        }
        if (blacklisted != null) {
            b.append(" and it should" + (blacklisted?" ":" not ")+ "be blacklisted");
        }
        return b.toString();
    }
}
