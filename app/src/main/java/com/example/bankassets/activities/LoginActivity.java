package com.example.bankassets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ImageView btnGoogle;
    private TextView tvRegister;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    private SharedPreferences loginPref, userPref;
    private static final String PREF_NAME = "login_pref";
    private static final String KEY_REMEMBER = "remember";
    private static final String PREF_USER  = "pref_user";
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase (dipakai untuk forgot password & Google)
        mAuth = FirebaseAuth.getInstance();

        // ===== INIT PREF =====
        loginPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        userPref  = getSharedPreferences(PREF_USER, MODE_PRIVATE);

        // ===== AUTO LOGIN (AMAN) =====
        if (loginPref.getBoolean(KEY_REMEMBER, false)
                && !userPref.getString("id_user", "").isEmpty()) {
            goToMain();
            return;
        }

        // Init View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvRegister = findViewById(R.id.tvRegister);

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // LOGIN MANUAL (MYSQL)
        btnLogin.setOnClickListener(v -> loginManual());

        // KE REGISTER
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        // GOOGLE LOGIN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    // ================= LOGIN MANUAL =================
    private void loginManual() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlLogin,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            String idUser   = obj.getString("id_user");
                            String username = obj.getString("username");
                            String emailRes = obj.getString("email");

                            userPref.edit()
                                    .putString("id_user", idUser)
                                    .putString("username", username)
                                    .putString("email", emailRes)
                                    .apply();
                            goToMain();
                        } else {
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this,
                        "Koneksi gagal",
                        Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ================= GOOGLE LOGIN =================
    private void signInWithGoogle() {
        startActivityForResult(
                googleSignInClient.getSignInIntent(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                saveGoogleUser(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign In gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveGoogleUser(GoogleSignInAccount account) {
        String email = account.getEmail();
        String username = account.getDisplayName();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlLoginGoogle,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            userPref.edit()
                                    .putString("id_user", obj.getString("id_user"))
                                    .putString("username", obj.getString("username"))
                                    .putString("email", obj.getString("email"))
                                    .apply();

                            loginPref.edit()
                                    .putBoolean(KEY_REMEMBER, true)
                                    .apply();

                            goToMain();

                        } else {
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Koneksi gagal", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("username", username);
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
