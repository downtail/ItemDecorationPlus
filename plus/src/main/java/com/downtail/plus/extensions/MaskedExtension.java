package com.downtail.plus.extensions;

import android.view.View;

public interface MaskedExtension {

    /**
     * 当前item是否需要遮罩
     *
     * @param position
     * @return
     */
    boolean isMaskedItem(int position);

    int getMaskedHeight(int position);

    View getMaskedView(int position);

}
