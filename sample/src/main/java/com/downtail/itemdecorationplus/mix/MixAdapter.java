package com.downtail.itemdecorationplus.mix;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.FloaterExtension;

import java.util.List;

public class MixAdapter extends BaseMultiItemQuickAdapter<MixEntity, BaseViewHolder> implements FloaterExtension {

    public MixAdapter(List<MixEntity> data) {
        super(data);
        addItemType(MixEntity.TYPE_HEADER, R.layout.item_mix_header);
        addItemType(MixEntity.TYPE_BODY, R.layout.item_mix_body);
    }

    @Override
    protected void convert(BaseViewHolder helper, MixEntity item) {
        int itemViewType = helper.getItemViewType();
        int position = helper.getAdapterPosition();
        if (itemViewType == MixEntity.TYPE_HEADER) {
            helper.setText(R.id.tv_mix_header, "ahha   " + position);
        } else if (itemViewType == MixEntity.TYPE_BODY) {
            helper.setText(R.id.tv_mix_body, position + "");
        }
    }

    @Override
    public boolean isFloaterView(int position) {
        return getItemViewType(position) == MixEntity.TYPE_HEADER;
    }

    @Override
    public int getItemType(int position) {
        return MixEntity.TYPE_HEADER;
    }
}
