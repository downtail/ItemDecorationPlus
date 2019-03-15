package com.downtail.itemdecorationplus.multiple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.downtail.itemdecorationplus.R;
import com.downtail.plus.decorations.MaskedItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ExpandableActivity extends AppCompatActivity {

    RecyclerView rvSample;
    ExpandableAdapter expandableAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        rvSample = findViewById(R.id.rv_sample);
        rvSample.setLayoutManager(new LinearLayoutManager(this));
//        rvSample.setLayoutManager(new GridLayoutManager(this, 2));
        List<MultiItemEntity> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MenuItem menuItem = new MenuItem();
            for (int j = 0; j < 15; j++) {
                menuItem.addSubItem(new SubItem());
            }
            data.add(menuItem);
        }
        expandableAdapter = new ExpandableAdapter(data);
        expandableAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (expandableAdapter.getItemViewType(position) == 0) {
                    MenuItem menuItem = (MenuItem) expandableAdapter.getData().get(position);
                    if (menuItem.isExpanded()) {
                        expandableAdapter.collapse(position);
                    } else {
                        expandableAdapter.expand(position);
                    }
                }
            }
        });
        rvSample.setAdapter(expandableAdapter);
        expandableAdapter.expandAll();

        MaskedItemDecoration maskedItemDecoration = MaskedItemDecoration.Builder
                .with(expandableAdapter)
                .setOnMaskedItemClickListener(new MaskedItemDecoration.OnMaskedItemClickListener() {
                    @Override
                    public void onMaskedItemClick(int position) {
                        if (expandableAdapter.getItemViewType(position) == 0) {
                            MenuItem menuItem = (MenuItem) expandableAdapter.getData().get(position);
                            if (menuItem.isExpanded()) {
                                expandableAdapter.collapse(position);
                            } else {
                                expandableAdapter.expand(position);
                            }
                        }
                    }
                })
                .build();
        rvSample.addItemDecoration(maskedItemDecoration);
    }
}
