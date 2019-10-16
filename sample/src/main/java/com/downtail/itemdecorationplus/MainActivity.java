package com.downtail.itemdecorationplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.downtail.itemdecorationplus.masked.MaskedActivity;
import com.downtail.itemdecorationplus.mix.MixActivity;
import com.downtail.itemdecorationplus.multiple.ExpandableActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnMasked, btnExpand, btnMix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMasked = findViewById(R.id.btn_masked);
        btnMasked.setOnClickListener(this);
        btnExpand = findViewById(R.id.btn_expand);
        btnExpand.setOnClickListener(this);
        btnMix = findViewById(R.id.btn_mix);
        btnMix.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_masked:
                startActivity(new Intent(this, MaskedActivity.class));
                break;
            case R.id.btn_expand:
                startActivity(new Intent(this, ExpandableActivity.class));
                break;
            case R.id.btn_mix:
                startActivity(new Intent(this, MixActivity.class));
                break;
        }
    }
}
