package com.example.bankassets.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.bankassets.R;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = getSharedPreferences("pref_account", MODE_PRIVATE);

        int themeMode = pref.getInt(
                "theme_mode",
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );

        AppCompatDelegate.setDefaultNightMode(themeMode);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences pref =
                newBase.getSharedPreferences("pref_user", MODE_PRIVATE);

        boolean accessibilityOn =
                pref.getBoolean("accessibility", false);

        Configuration config = new Configuration(newBase.getResources().getConfiguration());

        if (accessibilityOn) {
            config.fontScale = 1.2f; // BESAR
        } else {
            config.fontScale = 0.9f; // NORMAL
        }

        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    protected void setupBottomNav(String activeMenu) {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navMaintenance = findViewById(R.id.navMaintenance);
        LinearLayout navAccount = findViewById(R.id.navAccount);
        LinearLayout navSearch = findViewById(R.id.navSearch);

        ImageView icHome = findViewById(R.id.icHome);
        ImageView icMaintenance = findViewById(R.id.icMaintenance);
        ImageView icAccount = findViewById(R.id.icAccount);
        ImageView icSearch = findViewById(R.id.icSearch);


        TextView txtHome = findViewById(R.id.txtHome);
        TextView txtMaintenance = findViewById(R.id.txtMaintenance);
        TextView txtAccount = findViewById(R.id.txtAccount);
        TextView txtSearch = findViewById(R.id.txtSearch);


        resetNavColor(icHome, icMaintenance, icAccount, icSearch,
                txtHome, txtMaintenance, txtAccount, txtSearch);

        switch (activeMenu) {
            case "home":
                setActive(icHome, txtHome);
                break;
            case "search":
                setActive(icSearch, txtSearch);
                break;
            case "maintenance":
                setActive(icMaintenance, txtMaintenance);
                break;
            case "account":
                setActive(icAccount, txtAccount);
                break;
        }

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        navMaintenance.setOnClickListener(v -> {
            startActivity(new Intent(this, MaintenanceActivity.class));
            finish();
        });

        navAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });

        navSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        });
    }

    private void resetNavColor(ImageView icHome, ImageView icMaintenance, ImageView icAccount, ImageView icSearch,
                               TextView txtHome, TextView txtMaintenance, TextView txtAccount, TextView txtSearch) {

        int normal = getColor(R.color.text);

        icHome.setColorFilter(normal);
        icMaintenance.setColorFilter(normal);
        icAccount.setColorFilter(normal);
        icSearch.setColorFilter(normal);

        txtHome.setTextColor(normal);
        txtMaintenance.setTextColor(normal);
        txtAccount.setTextColor(normal);
        txtSearch.setTextColor(normal);
    }

    private void setActive(ImageView icon, TextView text) {
        int active = getColor(R.color.primaryBlue); // biru
        icon.setColorFilter(active);
        text.setTextColor(active);
    }
}
