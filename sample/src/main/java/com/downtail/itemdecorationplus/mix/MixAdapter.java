package com.downtail.itemdecorationplus.mix;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.SupportExtension;
import com.downtail.plus.utils.SizeUtil;

import java.util.List;

public class MixAdapter extends BaseMultiItemQuickAdapter<MixEntity, BaseViewHolder> implements SupportExtension {

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
    public boolean isSupportItem(int position) {
        return getItemViewType(position) == MixEntity.TYPE_HEADER;
    }

    @Override
    public int getSupportHeight(int position) {
        return SizeUtil.dip2px(mContext, 40);
    }

    @Override
    public View getSupportView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mix_header, null, false);
        TextView tvMix = view.findViewById(R.id.tv_mix_header);
        tvMix.setText("ahha   " + position);
        return view;
    }

    @Override
    public String getCacheKey(int position) {
        return String.valueOf(position);
    }
}
