package com.ignite.boycott.adapter;

import com.android.internal.util.Predicate;
import com.ignite.boycott.io.model.Maker;

/**
* Created by mdelegan on 12.02.14.
*/
class MakerContainsPredicate implements Predicate<Maker> {
    private String mFilter;

    MakerContainsPredicate(String mFilter) {
        this.mFilter = mFilter;
    }

    @Override
    public boolean apply(Maker maker) {
        String f = this.mFilter.toUpperCase();
        return maker.getBrand().toUpperCase().contains(f)
                || maker.getOwner().toUpperCase().contains(f);
    }
}
