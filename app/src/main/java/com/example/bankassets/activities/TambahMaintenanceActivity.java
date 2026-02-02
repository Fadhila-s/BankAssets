package com.example.bankassets.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.R;
import com.example.bankassets.Db_Contract;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahMaintenanceActivity extends BaseActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText tvTanggal, tvAktivitas, tvCatatan;
    private MaterialButton btnAdd;

    private String idAsset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_maintenance);

        // ===== Ambil id_asset =====
        idAsset = getIntent().getStringExtra("id_asset");

        if (idAsset == null) {
            Toast.makeText(this, "ID Asset tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }

        // ===== Toolbar =====
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== Form =====
        tvTanggal = findViewById(R.id.tvTanggal);
        tvAktivitas = findViewById(R.id.tvAktivitas);
        tvCatatan = findViewById(R.id.tvCatatan);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> simpanMaintenance());

        tvTanggal.setFocusable(false);
        tvTanggal.setOnClickListener(v -> showDatePicker());

    }

    private void simpanMaintenance() {

        String tanggal = tvTanggal.getText().toString().trim();
        String aktivitas_maintenance = tvAktivitas.getText().toString().trim();
        String catatan = tvCatatan.getText().toString().trim();

        if (tanggal.isEmpty()) {
            tvTanggal.setError("Tanggal wajib diisi");
            return;
        }

        if (aktivitas_maintenance.isEmpty()) {
            tvAktivitas.setError("Aktivitas maintenance wajib diisi");
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlAddMaintenanceDetail,
                response -> {
                    Toast.makeText(this,
                            "Maintenance berhasil ditambahkan",
                            Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK); // 🔥 trigger reload
                    finish();
                },
                error -> Toast.makeText(this,
                        "Gagal menambah maintenance",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_asset", String.valueOf(idAsset));
                p.put("tanggal", tanggal);
                p.put("aktivitas_maintenance", aktivitas_maintenance);
                p.put("catatan", catatan);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String tanggal = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth
                    );
                    tvTanggal.setText(tanggal);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

}
