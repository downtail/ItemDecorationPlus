package com.downtail.itemdecorationplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnMasked, btnCosmetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMasked = findViewById(R.id.btn_masked);
        btnMasked.setOnClickListener(this);
        btnCosmetic = findViewById(R.id.btn_cosmetic);
        btnCosmetic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_masked:
                startActivity(new Intent(this, MaskedActivity.class));
                break;
            case R.id.btn_cosmetic:
                startActivity(new Intent(this, CosmeticActivity.class));
                break;
        }
    }
}
