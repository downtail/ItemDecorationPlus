package com.downtail.plus.extensions;

import android.view.View;

public interface CosmeticExtension {

    boolean isCosmeticItem(int position);

    int getCosmeticHeight(int position);

    View getCosmeticView(int position);

}
