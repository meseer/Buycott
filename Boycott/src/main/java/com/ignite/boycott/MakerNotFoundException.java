package com.ignite.boycott;

import android.text.TextUtils;

/**
* Created by meseer on 16.01.14.
*/
class MakerNotFoundException extends RuntimeException {
    private final String barcode;
    private final String product;
    private final String maker;

    public MakerNotFoundException(String barcode, String maker, String product) {
        this.barcode = barcode;
        this.maker = maker;
        this.product = product;
    }

    @Override
    public String getMessage() {
        StringBuilder b = new StringBuilder("Maker not found for barcode ");
        b.append(barcode);
        if (TextUtils.isEmpty(maker)) {
            b.append(", made by '").append(maker).append("'");
        }
        if (TextUtils.isEmpty(product)) {
            b.append(". Repotedly product name is '").append(product).append("'");
        }
        return b.toString();
    }
}
