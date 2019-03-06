package com.downtail.itemdecorationplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        sampleAdapter = new SampleAdapter(this, getData());
        sampleAdapter.setOnItemClickListener(new SampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "position is " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rvSample.setAdapter(sampleAdapter);

        MaskedItemDecoration maskedItemDecoration = new MaskedItemDecoration(sampleAdapter);
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
