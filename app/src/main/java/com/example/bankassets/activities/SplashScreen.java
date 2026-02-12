package com.example.bankassets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.bankassets.R;

public class SplashScreen extends BaseActivity {

    private static final int SPLASH_DELAY = 3000; // 2.5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(() -> {

            SharedPreferences pref =
                    getSharedPreferences("pref_account", MODE_PRIVATE);

            boolean isLoggedIn =
                    pref.getBoolean("is_logged_in", false);

            if (isLoggedIn) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }

            finish();

        }, SPLASH_DELAY);
    }
}
