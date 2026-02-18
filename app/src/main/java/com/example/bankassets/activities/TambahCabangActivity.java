package com.example.bankassets.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.bankassets.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.bankassets.Db_Contract;

import java.util.HashMap;
import java.util.Map;

public class TambahCabangActivity extends BaseActivity {

    private TextInputEditText etNamaCabang, etAlamatCabang;
    private MaterialButton btnAdd;

    private TextView tvNamaCabang, tvAlamatCabang;
    private String idCabang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_cabang);

        // ===== Toolbar =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== Inisialisasi view =====
        etNamaCabang   = findViewById(R.id.etNamaCabang);
        etAlamatCabang = findViewById(R.id.etAlamatCabang);
        btnAdd         = findViewById(R.id.btnAdd);

        tvNamaCabang   = findViewById(R.id.namaCabang);
        tvAlamatCabang = findViewById(R.id.alamatCabang);

        SharedPreferences prefs = getSharedPreferences("pref_selected_cabang", MODE_PRIVATE);
        idCabang = prefs.getString("id_cabang", null);

        String nama = prefs.getString("nama_cabang", "-");
        String alamat = prefs.getString("alamat_cabang", "-");

        etNamaCabang.setText(nama);
        etAlamatCabang.setText(alamat);

        btnAdd.setOnClickListener(v -> tambahCabang());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCabangAktif();
    }

    private void loadCabangAktif() {
        SharedPreferences prefs = getSharedPreferences("pref_selected_cabang", MODE_PRIVATE);

        String nama_cabang   = prefs.getString("nama_cabang", "Belum dipilih");
        String alamat_cabang = prefs.getString("alamat_cabang", "-");

        tvNamaCabang.setText(nama_cabang);
        tvAlamatCabang.setText(alamat_cabang);
    }


    private void tambahCabang() {
        String nama_cabang   = etNamaCabang.getText().toString().trim();
        String alamat_cabang = etAlamatCabang.getText().toString().trim();

        if (nama_cabang.isEmpty() || alamat_cabang.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlAddCabang,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(this, "Kantor berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // ⬅️ penting
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal menambah kantor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama_cabang", nama_cabang);
                params.put("alamat_cabang", alamat_cabang);

                Log.d("POST_DEBUG",
                        "nama=" + nama_cabang + ", alamat=" + alamat_cabang);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
