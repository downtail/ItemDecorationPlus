package com.downtail.itemdecorationplus.masked;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.downtail.itemdecorationplus.R;
import com.downtail.plus.decorations.FloaterView;
import com.downtail.plus.decorations.FloaterItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MaskedActivity extends AppCompatActivity implements OnRefreshLoadMoreListener {

    SmartRefreshLayout refreshLayout;
    RecyclerView rvSample;
    MaskedAdapter maskedAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshLoadMoreListener(this);

        rvSample = findViewById(R.id.rv_sample);
        rvSample.setLayoutManager(new LinearLayoutManager(this));
        maskedAdapter = new MaskedAdapter(this, getData());
        maskedAdapter.setOnItemClickListener(new MaskedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MaskedActivity.this, "position is " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rvSample.setAdapter(maskedAdapter);

        FloaterView floaterView = FloaterView.init(rvSample)
                .addItemType(0, R.layout.item_masked, R.id.tv_sample)
                .setOnItemClickListener(new FloaterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                        Log.v("ysj", view.getId() + " " + position);
                    }
                })
                .setOnBindViewListener(new FloaterView.OnBindViewListener() {
                    @Override
                    public void onBind(View view, int position) {
                        TextView tvSample = view.findViewById(R.id.tv_sample);
                        tvSample.setText("gaga" + " " + position);
                        view.setBackgroundColor(Color.parseColor("#ff0000"));
                    }
                })
                .setOnItemChildClickListener(new FloaterView.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(View view, int position) {
                        switch (view.getId()) {
                            case R.id.tv_sample:
                                Log.v("ysj", view.getId() + " tv_sample " + position);
                                break;
                        }
                    }
                });
        FloaterItemDecoration floaterItemDecoration = new FloaterItemDecoration(maskedAdapter, floaterView);
        rvSample.addItemDecoration(floaterItemDecoration);
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            data.add(i + "");
        }
        return data;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore(2000);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(2000);
    }
}
