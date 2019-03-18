package com.downtail.itemdecorationplus.mix;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.CosmeticExtension;
import com.downtail.plus.extensions.MaskedExtension;
import com.downtail.plus.utils.SizeUtil;

import java.util.List;

public class MixAdapter extends BaseMultiItemQuickAdapter<MixEntity, BaseViewHolder> implements MaskedExtension, CosmeticExtension {

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
    public boolean isMaskedItem(int position) {
        return getItemViewType(position) == MixEntity.TYPE_HEADER;
    }

    @Override
    public int getMaskedHeight(int position) {
        return SizeUtil.dip2px(mContext, 40);
    }

    @Override
    public View getMaskedView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mix_header, null, false);
        TextView tvMix = view.findViewById(R.id.tv_mix_header);
        tvMix.setText("ahha   " + position);
        return view;
    }

    @Override
    public boolean isCosmeticItem(int position) {
        return getItemViewType(position) == MixEntity.TYPE_HEADER;
    }

    @Override
    public int getCosmeticHeight(int position) {
        return SizeUtil.dip2px(mContext, 40);
    }

    @Override
    public View getCosmeticView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mix_header, null, false);
        TextView tvMix = view.findViewById(R.id.tv_mix_header);
        tvMix.setText("ahha");
        view.setBackgroundColor(Color.parseColor("#ff0000"));
        return view;
    }
}