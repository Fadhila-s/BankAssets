package com.example.bankassets.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class TambahDivisiActivity extends BaseActivity {

    private TextInputEditText etNamaDivisi, etRuangan;
    private MaterialButton btnAdd;

    private TextView tvNamaCabang, tvAlamatCabang;
    private String idCabang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_divisi);

        // ===== Toolbar =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== Ambil cabang aktif =====
        SharedPreferences prefs = getSharedPreferences("pref_selected_cabang", MODE_PRIVATE);
        idCabang = prefs.getString("id_cabang", null);

        if (idCabang == null) {
            Toast.makeText(this, "Cabang belum dipilih", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== Inisialisasi view =====
        etNamaDivisi = findViewById(R.id.etNamaDivisi);
        etRuangan    = findViewById(R.id.etRuangan);
        btnAdd       = findViewById(R.id.btnAdd);

        tvNamaCabang   = findViewById(R.id.namaCabang);
        tvAlamatCabang = findViewById(R.id.alamatCabang);

        // ===== Add Button =====
        btnAdd.setOnClickListener(v -> tambahDivisi());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCabangAktif();
    }

    private void loadCabangAktif() {
        SharedPreferences prefs = getSharedPreferences("pref_selected_cabang", MODE_PRIVATE);

        idCabang = prefs.getString("id_cabang", null);
        String namaCabang   = prefs.getString("nama_cabang", "Belum dipilih");
        String alamatCabang = prefs.getString("alamat_cabang", "-");

        if (idCabang == null) {
            Toast.makeText(this, "Cabang belum dipilih", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvNamaCabang.setText(namaCabang);
        tvAlamatCabang.setText(alamatCabang);
    }

    private void tambahDivisi() {
        String nama_divisi    = etNamaDivisi.getText().toString().trim();
        String ruangan_divisi = etRuangan.getText().toString().trim();

        if (nama_divisi.isEmpty() || ruangan_divisi.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAdd.setEnabled(false);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlAddDivisi,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(this, "Divisi berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal menambah divisi", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cabang", idCabang);
                params.put("nama_divisi", nama_divisi);
                params.put("ruangan_divisi", ruangan_divisi);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
