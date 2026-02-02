package com.example.bankassets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private SharedPreferences loginPref, userPref;
    private static final String PREF_NAME = "login_pref";
    private static final String KEY_REMEMBER = "remember";
    private static final String PREF_USER  = "pref_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ===== INIT PREF =====
        loginPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        userPref  = getSharedPreferences(PREF_USER, MODE_PRIVATE);

        // ===== AUTO LOGIN (AMAN) =====
        if (loginPref.getBoolean(KEY_REMEMBER, false)
                && !userPref.getString("id_user", "").isEmpty()) {
            goToMain();
            return;
        }

        // Bind view
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Register email/password
        btnRegister.setOnClickListener(v -> registerUser());

        // Login text
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password min 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password not match");
            return;
        }

        // SIMPAN KE MYSQL
        saveUserToDatabase(username, email, password);
    }

    private void saveUserToDatabase(String username, String email, String password) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlRegister,
                response -> {
                    try {
                        response = response.trim();
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            String idUser = obj.getString("id_user");

                            // ===== SIMPAN SESSION =====
                            userPref.edit()
                                    .putString("id_user", idUser)
                                    .putString("username", username)
                                    .putString("email", email)
                                    .apply();

                            Toast.makeText(this,
                                    "Registrasi berhasil",
                                    Toast.LENGTH_SHORT).show();

                            goToMain();

                        } else {
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                "Response error",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this,
                        "Koneksi gagal",
                        Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ================= KE MAIN =================
    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
