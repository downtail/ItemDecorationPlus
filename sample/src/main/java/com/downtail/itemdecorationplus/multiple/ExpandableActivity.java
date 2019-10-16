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
import com.downtail.plus.decorations.FloaterView;
import com.downtail.plus.decorations.FloaterItemDecoration;

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

        FloaterView floaterView = FloaterView.init(rvSample)
                .addItemType(0, R.layout.item_menu, R.id.tv_menu)
                .setOnBindViewListener(new FloaterView.OnBindViewListener() {
                    @Override
                    public void onBind(View view, int position) {

                    }
                })
                .setOnItemChildClickListener(new FloaterView.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(View view, int position) {
                        switch (view.getId()) {
                            case R.id.tv_menu:
                                MenuItem menuItem = (MenuItem) expandableAdapter.getData().get(position);
                                if (menuItem.isExpanded()) {
                                    expandableAdapter.collapse(position);
                                } else {
                                    expandableAdapter.expand(position);
                                }
                                break;
                        }
                    }
                });
        FloaterItemDecoration floaterItemDecoration = new FloaterItemDecoration(expandableAdapter, floaterView);
        rvSample.addItemDecoration(floaterItemDecoration);
    }
}
