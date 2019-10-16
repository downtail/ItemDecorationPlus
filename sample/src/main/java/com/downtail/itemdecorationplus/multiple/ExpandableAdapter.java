package com.downtail.itemdecorationplus.multiple;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.FloaterExtension;

import java.util.List;

public class ExpandableAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> implements FloaterExtension {

    public ExpandableAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(0, R.layout.item_menu);
        addItemType(1, R.layout.item_sub);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        int itemViewType = helper.getItemViewType();
        final int position = helper.getAdapterPosition();
        switch (itemViewType) {
            case 0:
                helper.setText(R.id.tv_menu, "这是头部" + position);
                break;
            case 1:
                helper.setText(R.id.tv_sub, "" + position);
                break;
        }
    }

    @Override
    public boolean isFloaterView(int position) {
        return getItemViewType(position) == 0;
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
