package com.downtail.itemdecorationplus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.downtail.plus.decorations.CosmeticItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CosmeticActivity extends AppCompatActivity {

    RecyclerView rvSample;
    CosmeticAdapter cosmeticAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        rvSample = findViewById(R.id.rv_sample);
        rvSample.setLayoutManager(new GridLayoutManager(this,2));
        cosmeticAdapter = new CosmeticAdapter(CosmeticActivity.this, getData());
        cosmeticAdapter.setOnItemClickListener(new CosmeticAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(CosmeticActivity.this, "ahha" + position, Toast.LENGTH_SHORT).show();
            }
        });
        rvSample.setAdapter(cosmeticAdapter);

        CosmeticItemDecoration cosmeticItemDecoration = CosmeticItemDecoration.Builder
                .with(cosmeticAdapter)
                .build();
        rvSample.addItemDecoration(cosmeticItemDecoration);
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            data.add(i + "");
        }
        return data;
    }

}
