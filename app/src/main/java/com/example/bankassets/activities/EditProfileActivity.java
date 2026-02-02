package com.example.bankassets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnSave;
    private TextView tvChangePassword;
    private String idUser;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 🔑 SharedPreferences (SAMAKAN DENGAN ChangePasswordActivity)
        prefs = getSharedPreferences("pref_user", MODE_PRIVATE);

        // ===== Toolbar =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== Init Views =====
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        tvChangePassword = findViewById(R.id.tvChangePassword);

        idUser = prefs.getString("id_user", "");
        if (idUser.isEmpty()) {
            Toast.makeText(this, "Session User tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // ===== Load user =====
        loadUser();

        // ===== Save button =====
        btnSave.setOnClickListener(v -> saveProfile());

        // ===== Change password =====
        tvChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class))
        );
    }

    /**
     * 🔄 Dipanggil setiap kembali dari ChangePasswordActivity
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPassword();
    }

    private void loadUser() {
        String username = prefs.getString("username", "");
        etUsername.setText(username);
        loadPassword();
    }

    private void loadPassword() {
        String password = prefs.getString("password", "");
        etPassword.setText(password);
    }

    private void saveProfile() {
        String username = etUsername.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlUpdateAccount,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        Toast.makeText(this,
                                obj.getString("message"),
                                Toast.LENGTH_SHORT).show();

                        if (obj.getString("status").equals("success")) {
                            prefs.edit()
                                    .putString("username", username)
                                    .apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user", idUser);
                params.put("username",username);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
