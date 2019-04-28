package com.downtail.itemdecorationplus.mix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.downtail.itemdecorationplus.R;
import com.downtail.plus.decorations.CosmeticItemDecoration;
import com.downtail.plus.decorations.MaskedItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MixActivity extends AppCompatActivity {

    RecyclerView rvSample;
    MixAdapter mixAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        rvSample = findViewById(R.id.rv_sample);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        final int spanCount = gridLayoutManager.getSpanCount();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (mixAdapter.getItemViewType(i) == MixEntity.TYPE_HEADER) {
                    return spanCount;
                }
                return 1;
            }
        });

        mixAdapter = new MixAdapter(null);
        rvSample.setAdapter(mixAdapter);
        rvSample.setLayoutManager(gridLayoutManager);

        MaskedItemDecoration maskedItemDecoration = MaskedItemDecoration.Builder
                .with(mixAdapter)
                .setOnMaskedItemClickListener(new MaskedItemDecoration.OnMaskedItemClickListener() {
                    @Override
                    public void onMaskedItemClick(int position) {
                        Toast.makeText(MixActivity.this, "ahha", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        rvSample.addItemDecoration(maskedItemDecoration);

        CosmeticItemDecoration cosmeticItemDecoration=CosmeticItemDecoration.Builder
                .with(mixAdapter)
                .setOnCosmeticItemClickListener(new CosmeticItemDecoration.OnCosmeticItemClickListener() {
                    @Override
                    public void onCosmeticItemClick(int position) {
                        Toast.makeText(MixActivity.this, "ahha", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
//        rvSample.addItemDecoration(cosmeticItemDecoration);

        List<MixEntity> data = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            data.add(new MixEntity(MixEntity.TYPE_HEADER));
            for (int j = 0; j < 13; j++) {
                data.add(new MixEntity(MixEntity.TYPE_BODY));
            }
        }
        mixAdapter.setNewData(data);
    }

}
