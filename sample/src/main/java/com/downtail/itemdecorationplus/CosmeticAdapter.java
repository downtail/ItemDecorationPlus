package com.downtail.itemdecorationplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.downtail.plus.extensions.CosmeticExtension;

import java.util.List;

public class CosmeticAdapter extends RecyclerView.Adapter<CosmeticAdapter.CosmeticHolder> implements CosmeticExtension {

    private Context context;
    private List<String> data;

    public CosmeticAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CosmeticHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CosmeticHolder(LayoutInflater.from(context).inflate(R.layout.item_cosmetic, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CosmeticHolder cosmeticHolder, int i) {
        cosmeticHolder.tvCosmetic.setText(data.get(i));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class CosmeticHolder extends RecyclerView.ViewHolder {
        TextView tvCosmetic;

        public CosmeticHolder(@NonNull View itemView) {
            super(itemView);
            tvCosmetic = itemView.findViewById(R.id.tv_cosmetic);
        }
    }

    @Override
    public boolean isCosmeticItem(int position) {
        return position % 5 == 1;
    }

    @Override
    public View getCosmeticView(int position) {
        return LayoutInflater.from(context).inflate(R.layout.layout_fill, null, false);
    }

}
