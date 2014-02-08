package com.ignite.boycott.reporting.crashlytics;

import android.text.TextUtils;

/**
* Created by meseer on 16.01.14.
*/
class MakerNotFoundException extends RuntimeException {
    public MakerNotFoundException(String barcode, String maker, String product, Boolean blacklisted) {
        super(buildMessage(barcode, maker, product, blacklisted));
    }

    private static String buildMessage(String barcode, String maker, String product, Boolean blacklisted) {
        StringBuilder b = new StringBuilder("Maker not found for barcode ");
        b.append(barcode);
        if (!TextUtils.isEmpty(maker)) {
            b.append(", made by '").append(maker).append("'");
        }
        if (!TextUtils.isEmpty(product)) {
            b.append(". Repotedly product name is '").append(product).append("'");
        }
        if (blacklisted != null) {
            b.append(" and it should" + (blacklisted ? " " : " not ") + "be blacklisted");
        }
        return b.toString();
    }
}
