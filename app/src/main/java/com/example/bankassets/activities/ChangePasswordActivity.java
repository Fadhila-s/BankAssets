package com.example.bankassets.activities; // ganti sesuai package kamu

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

public class ChangePasswordActivity extends BaseActivity {

    private TextInputEditText passwordLama, passwordBaru, passwordConfirm;
    private MaterialButton btnSave;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password); // ganti sesuai nama XML-mu

        prefs = getSharedPreferences("pref_user", Context.MODE_PRIVATE);

        // Bind views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        passwordLama = findViewById(R.id.passwordLama);
        passwordBaru = findViewById(R.id.passwordBaru);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        btnSave = findViewById(R.id.btnSave);

        // Toolbar back button
        toolbar.setNavigationOnClickListener(v -> finish());

        // Tombol Save
        btnSave.setOnClickListener(v -> savePassword());
    }

    private void savePassword() {
        String lama = passwordLama.getText().toString().trim();
        String baru = passwordBaru.getText().toString().trim();
        String confirm = passwordConfirm.getText().toString().trim();
        String idUser = prefs.getString("id_user", "");

        // Validasi input
        if (TextUtils.isEmpty(lama) || TextUtils.isEmpty(baru) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!baru.equals(confirm)) {
            Toast.makeText(this, "Password baru dan konfirmasi tidak sama!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlUpdatePassword,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        String message = obj.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status.equals("success")) {
                            prefs.edit()
                                    .putString("password", baru)
                                    .apply();
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi server", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user", idUser);
                params.put("password_lama", lama);
                params.put("password_baru", baru);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
        // Kembali ke EditProfileActivity
        finish();
    }
}
