package com.downtail.itemdecorationplus.masked;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.downtail.itemdecorationplus.R;
import com.downtail.plus.extensions.FloaterExtension;
import com.downtail.plus.extensions.SupportExtension;
import com.downtail.plus.utils.SizeUtil;

import java.util.List;

public class MaskedAdapter extends RecyclerView.Adapter<MaskedAdapter.SampleHolder> implements SupportExtension, FloaterExtension {


    private Context context;
    private List<String> data;
    private OnItemClickListener onItemClickListener;

    public MaskedAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public SampleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SampleHolder(LayoutInflater.from(context).inflate(R.layout.item_masked, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SampleHolder sampleHolder, final int i) {
        sampleHolder.tvSample.setText(i + "");
        if (i % 6 == 1) {
            sampleHolder.tvSample.setBackgroundColor(Color.parseColor("#ff0000"));
        } else {
            sampleHolder.tvSample.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        sampleHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

    class SampleHolder extends RecyclerView.ViewHolder {
        TextView tvSample;

        public SampleHolder(@NonNull View itemView) {
            super(itemView);
            tvSample = itemView.findViewById(R.id.tv_sample);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public boolean isSupportItem(int position) {
        return position % 6 == 1;
    }

    @Override
    public int getSupportHeight(int position) {
        return SizeUtil.dip2px(context, 50);
    }

    @Override
    public View getSupportView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_masked, null, false);
        view.setBackgroundColor(Color.parseColor("#ff0000"));
        TextView textView = view.findViewById(R.id.tv_sample);
        textView.setText(position + "");
        return view;
    }

    @Override
    public String getCacheKey(int position) {
        return String.valueOf(position);
    }

    @Override
    public boolean isFloaterView(int position) {
        return position % 6 == 1;
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
