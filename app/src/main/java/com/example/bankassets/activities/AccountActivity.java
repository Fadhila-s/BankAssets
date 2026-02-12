package com.example.bankassets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.example.bankassets.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AccountActivity extends BaseActivity {

    private ImageView arrowRight1, arrowRight2;
    private Switch themeModeSwitch, notificationSwitch, accessibilitySwitch;

    private MaterialToolbar toolbar;
    private LinearLayout profileLayout;
    private CardView cardContainer;

    private TextView usernameTV, emailTV, icProfileTV;
    private SharedPreferences userPref, settingPref;
    private static final String PREF_ACCOUNT = "pref_account";
    private static final String KEY_THEME = "theme_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingPref = getSharedPreferences(PREF_ACCOUNT, MODE_PRIVATE);
        int themeMode = settingPref.getInt(
                KEY_THEME,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );
        AppCompatDelegate.setDefaultNightMode(themeMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        LinearLayout logoutLayout = findViewById(R.id.container6);

        logoutLayout.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(AccountActivity.this)
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Yakin ingin logout dari akun?")
                    .setCancelable(false)

                    .setPositiveButton("Ya", (dialog, which) -> {

                        // Hapus login lama
                        SharedPreferences loginPref =
                                getSharedPreferences("login_pref", MODE_PRIVATE);
                        loginPref.edit().clear().apply();

                        // 🔥 Update flag untuk Splash
                        SharedPreferences accountPref =
                                getSharedPreferences("pref_account", MODE_PRIVATE);
                        accountPref.edit()
                                .putBoolean("is_logged_in", false)
                                .apply();

                        // Pindah ke Login
                        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })

                    .setNegativeButton("Tidak", (dialog, which) -> {
                        dialog.dismiss();
                    })

                    .show();
        });


        userPref = getSharedPreferences("pref_user", MODE_PRIVATE);

        cardContainer = findViewById(R.id.cardContainer);

        usernameTV = findViewById(R.id.Username);
        emailTV = findViewById(R.id.Email);
        icProfileTV = findViewById(R.id.icProfile);

        arrowRight1 = findViewById(R.id.arrowRight1);
        arrowRight2 = findViewById(R.id.arrowRight2);

        settingPref = getSharedPreferences("pref_account", MODE_PRIVATE);
        themeModeSwitch = findViewById(R.id.themeMode);

        // ===== SYNC SWITCH DENGAN KONDISI =====
        int savedMode = settingPref.getInt(KEY_THEME, -1);

        boolean isDark;

        if (savedMode == AppCompatDelegate.MODE_NIGHT_YES) {
            isDark = true;
        } else if (savedMode == AppCompatDelegate.MODE_NIGHT_NO) {
            isDark = false;
        } else {
            // FOLLOW SYSTEM → cek HP
            isDark = isSystemDarkMode();
        }

        themeModeSwitch.setChecked(isDark);

        // ===== SWITCH LISTENER =====
        themeModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            int newMode = isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO;

            settingPref.edit()
                    .putInt(KEY_THEME, newMode)
                    .apply();

            AppCompatDelegate.setDefaultNightMode(newMode);
            recreate(); // apply langsung
        });

        notificationSwitch = findViewById(R.id.Notification);
        accessibilitySwitch = findViewById(R.id.Accessibility);

        // ===== LOAD PREF =====
        notificationSwitch.setChecked(userPref.getBoolean("notification", true));
        accessibilitySwitch.setChecked(userPref.getBoolean("accessibility", false));

        // ===== EDIT PROFILE =====
        arrowRight1.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        // ===== ABOUT US =====
        arrowRight2.setOnClickListener(v ->
                startActivity(new Intent(this, AboutUsActivity.class)));

        // ===== SWITCH NOTIFICATION =====
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userPref.edit().putBoolean("notification", isChecked).apply();
            Toast.makeText(this,
                    isChecked
                            ? "Notifikasi aset kritis diaktifkan"
                            : "Notifikasi aset kritis dimatikan",
                    Toast.LENGTH_SHORT).show();
        });

        // ===== SWITCH ACCESSIBILITY =====
        accessibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userPref.edit().putBoolean("accessibility", isChecked).apply();
            recreate(); // apply langsung
            Toast.makeText(this,
                    isChecked
                            ? "Mode aksesibilitas diaktifkan"
                            : "Mode aksesibilitas dimatikan",
                    Toast.LENGTH_SHORT).show();
        });

        String username = userPref.getString("username", "User");
        String email = userPref.getString("email", "user@email.com");

        usernameTV.setText(username.isEmpty() ? "Unknown User" : username);
        emailTV.setText(email.isEmpty() ? "unknown@email.com" : email);
        icProfileTV.setText(username.isEmpty() ? "?" : String.valueOf(username.charAt(0)).toUpperCase());
        setupBottomNav("account");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private boolean isSystemDarkMode() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
