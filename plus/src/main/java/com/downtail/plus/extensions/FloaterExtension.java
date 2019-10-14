package com.downtail.plus.extensions;

import android.support.annotation.IntRange;

public interface FloaterExtension {

    boolean isFloaterView(int position);

    @IntRange(from = 0, to = Integer.MAX_VALUE)
    int getItemType(int position);
}
