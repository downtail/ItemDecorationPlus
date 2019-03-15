package com.downtail.itemdecorationplus.multiple;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MenuItem extends AbstractExpandableItem<SubItem> implements MultiItemEntity {

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
