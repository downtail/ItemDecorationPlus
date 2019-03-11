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
    private OnItemClickListener onItemClickListener;

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
    public void onBindViewHolder(@NonNull CosmeticHolder cosmeticHolder, final int i) {
        cosmeticHolder.tvCosmetic.setText(data.get(i));
        cosmeticHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(i);
                }
            }
        });
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
        return position % 7 == 1 || position == 2;
    }

    @Override
    public int getCosmeticHeight(int position) {
        return 200;
    }

    @Override
    public View getCosmeticView(int position) {
        return LayoutInflater.from(context).inflate(R.layout.layout_fill, null, false);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
