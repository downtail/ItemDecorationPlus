package com.downtail.itemdecorationplus.mix;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MixEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_BODY = 1;

    private int itemType;

    public MixEntity(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
