package com.downtail.itemdecorationplus.cosmetic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.SupportExtension;

import java.util.List;

public class CosmeticAdapter extends RecyclerView.Adapter<CosmeticAdapter.CosmeticHolder> implements SupportExtension {

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
    public boolean isSupportItem(int position) {
        return position % 7 == 1;
    }

    @Override
    public int getSupportHeight(int position) {
        return 200;
    }

    @Override
    public View getSupportView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_fill, null, false);
        TextView textView = view.findViewById(R.id.tv_fill);
        textView.setText(position + "ahha");
        return view;
    }

    @Override
    public String getCacheKey(int position) {
        return String.valueOf(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
