package com.downtail.itemdecorationplus.brvah;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.MaskedExtension;
import com.downtail.plus.utils.SizeUtil;

import java.util.List;

public class ExpandableAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> implements MaskedExtension {

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
    public boolean isMaskedItem(int position) {
        return getItemViewType(position) == 0;
    }

    @Override
    public int getMaskedHeight(int position) {
        return SizeUtil.dip2px(mContext, 50);
    }

    @Override
    public View getMaskedView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_menu, null, false);
        TextView textView = view.findViewById(R.id.tv_menu);
        textView.setText("这是头部" + position);
        return view;
    }

}
