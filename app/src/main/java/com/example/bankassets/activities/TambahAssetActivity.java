package com.example.bankassets.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TambahAssetActivity extends BaseActivity {

    private TextInputEditText etNamaAsset, etSpesifikasi, etKendala, etPic;
    private MaterialAutoCompleteTextView ddJenisAsset, ddKondisiAsset, ddStatusPenggunaan;
    private MaterialButton btnSimpan;

    private ImageView icDropdownJenisAsset;
    private ImageView icDropdownKondisi;
    private ImageView icDropdownStatusPenggunaan;

    private String idDivisi;

    // ✅ GLOBAL MAP
    private Map<String, String> mapJenis = new HashMap<>();
    private String idJenisTerpilih = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_asset);

        // ===== Toolbar =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== View =====
        etNamaAsset   = findViewById(R.id.etNamaAsset);
        etSpesifikasi = findViewById(R.id.etSpesifikasi);
        etKendala     = findViewById(R.id.etKendala);
        etPic         = findViewById(R.id.etPic);

        ddJenisAsset   = findViewById(R.id.etJenisAsset);
        ddKondisiAsset = findViewById(R.id.etKondisiAsset);
        ddStatusPenggunaan = findViewById(R.id.etStatusPenggunaan);

        btnSimpan = findViewById(R.id.btnAdd);
        icDropdownJenisAsset = findViewById(R.id.icDropdownJenisAsset);
        icDropdownKondisi = findViewById(R.id.icDropdownKondisi);
        icDropdownStatusPenggunaan = findViewById(R.id.icDropdownStatusPenggunaan);


        // ===== Ambil id_divisi =====
        idDivisi = getIntent().getStringExtra("id_divisi");
        Log.d("DIVISI_DEBUG", "TambahAsset id_divisi = " + idDivisi);

        if (idDivisi == null) {
            Toast.makeText(this, "Divisi tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupDropdown();
        // ✅ KLIK ICON UNTUK BUKA DROPDOWN
        icDropdownJenisAsset.setOnClickListener(v -> ddJenisAsset.showDropDown());
        icDropdownKondisi.setOnClickListener(v -> ddKondisiAsset.showDropDown());
        icDropdownStatusPenggunaan.setOnClickListener(v -> ddStatusPenggunaan.showDropDown());


        btnSimpan.setOnClickListener(v -> simpanAsset());
    }

    private void setupDropdown() {

        // ===== Jenis Asset =====
        Map<String, String> mapJenis = new HashMap<>();
        mapJenis.put("Laptop", "1");
        mapJenis.put("PC", "2");
        mapJenis.put("Printer", "3");
        mapJenis.put("Telepon", "4");
        mapJenis.put("Meja", "5");
        mapJenis.put("Kursi", "6");
        mapJenis.put("Lemari", "7");
        mapJenis.put("Lainnya", "8");

        String[] jenisAsset = mapJenis.keySet().toArray(new String[0]);
        ddJenisAsset.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                jenisAsset
        ));

        // ✅ SIMPAN id_jenis SAAT DIPILIH
        ddJenisAsset.setOnItemClickListener((parent, view, position, id) -> {
            String jenisDipilih = parent.getItemAtPosition(position).toString();
            idJenisTerpilih = mapJenis.get(jenisDipilih);
        });

        // ===== Kondisi Asset =====
        String[] kondisiAsset = {"Baik", "Peringatan", "Kritis", "Gudang"};

        ddKondisiAsset.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        kondisiAsset)
        );

        String[] statusPenggunaan = {"Aktif", "Nonaktif"};

        ddStatusPenggunaan.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        statusPenggunaan)
        );
    }

    private void simpanAsset() {
        String nama   = etNamaAsset.getText().toString().trim();
        String spek   = etSpesifikasi.getText().toString().trim();
        String kendala= etKendala.getText().toString().trim();
        String pic    = etPic.getText().toString().trim();
        String kondisi= ddKondisiAsset.getText().toString().trim();
        String status = ddStatusPenggunaan.getText().toString().trim();


        if (nama.isEmpty() || idJenisTerpilih == null || kondisi.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua field wajib", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlAddAsset,
                response -> {
                    Log.d("RESPONSE_SERVER", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            Toast.makeText(this, "Asset berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, "Gagal menambah asset", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", e.toString());
                        Toast.makeText(this, "Response tidak valid", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_divisi", idDivisi);
                p.put("nama_asset", nama);
                p.put("spesifikasi_asset", spek);
                p.put("kendala_asset", kendala);
                p.put("pic_asset", pic);
                p.put("kondisi_asset", kondisi);
                p.put("status_penggunaan", status);
                p.put("id_jenis", idJenisTerpilih); // ✅ BENAR
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
