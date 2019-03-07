package com.downtail.itemdecorationplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.downtail.plus.decorations.MaskedItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvSample;
    SampleAdapter sampleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvSample = findViewById(R.id.rv_sample);
        rvSample.setLayoutManager(new LinearLayoutManager(this));
//        rvSample.setLayoutManager(new GridLayoutManager(this,2));
        sampleAdapter = new SampleAdapter(this, getData());
        sampleAdapter.setOnItemClickListener(new SampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "position is " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rvSample.setAdapter(sampleAdapter);

        MaskedItemDecoration maskedItemDecoration = MaskedItemDecoration.Builder
                .with(sampleAdapter)
                .setOnMaskedItemClickListener(new MaskedItemDecoration.OnMaskedItemClickListener() {
                    @Override
                    public void onMaskedItemClick(int position) {
                        Toast.makeText(MainActivity.this, "你点击了item " + position, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnMaskedViewClickListener(new MaskedItemDecoration.OnMaskedViewClickListener() {
                    @Override
                    public void onMaskedViewClick(View view, int position) {
                        if (view.getId() == R.id.tv_sample) {
                            Toast.makeText(MainActivity.this, "你点击了sample" + position, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        rvSample.addItemDecoration(maskedItemDecoration);
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            data.add(i + "");
        }
        return data;
    }

}
