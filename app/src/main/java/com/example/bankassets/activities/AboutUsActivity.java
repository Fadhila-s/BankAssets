package com.example.bankassets.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.bankassets.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // ===== Toolbar =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            // Kembali ke AccountActivity
            Intent intent = new Intent(AboutUsActivity.this, AccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
