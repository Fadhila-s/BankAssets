package com.example.bankassets.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.example.bankassets.adapter.AssetAdapter;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.utils.AssetNotificationChecker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetRuangan extends BaseActivity {

    private RecyclerView rvAsset;
    private AssetAdapter assetAdapter;
    private List<AssetModel> assetList;

    private ImageView icDeleteAsset;
    private AssetModel selectedAsset;

    private TextView tvNamaCabang, tvAlamatCabang;
    private TextView tvNamaDivisi, tvRuanganDivisi;

    private ChipGroup chipGroup;
    private Chip chipSemua; // ✅ WAJIB
    private String idDivisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_ruangan);

        // ===== Views =====
        rvAsset = findViewById(R.id.rvAsset);
        FloatingActionButton fabAdd = findViewById(R.id.btnAddAsset);

        tvAlamatCabang = findViewById(R.id.alamatCabang);
        tvNamaCabang   = findViewById(R.id.namaCabang);
        tvNamaDivisi   = findViewById(R.id.tvNamaDivisi);
        tvRuanganDivisi= findViewById(R.id.tvRuanganDivisi);

        icDeleteAsset = findViewById(R.id.icDeleteAsset);
        icDeleteAsset.setVisibility(View.GONE);

        chipGroup = findViewById(R.id.chipGroup);
        chipSemua = findViewById(R.id.chipSemua);

        // DEFAULT FILTER = SEMUA
        chipSemua.setChecked(true);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== RecyclerView =====
        assetList = new ArrayList<>();
        assetAdapter = new AssetAdapter(this, assetList);
        rvAsset.setLayoutManager(new LinearLayoutManager(this));
        rvAsset.setAdapter(assetAdapter);

        // LONG CLICK
        assetAdapter.setOnLongClickListener(asset -> {
            selectedAsset = asset;
            assetAdapter.setSelectedAsset(asset);
            icDeleteAsset.setVisibility(View.VISIBLE);
        });

        icDeleteAsset.setOnClickListener(v -> showDeleteDialog());

        // ===== FAB =====
        fabAdd.setOnClickListener(v -> showAddDialog());

        // ===== Ambil DIVISI =====
        idDivisi = getIntent().getStringExtra("id_divisi");

        if (idDivisi == null) {
            Toast.makeText(this, "Divisi tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvNamaDivisi.setText(getIntent().getStringExtra("nama_divisi"));
        tvRuanganDivisi.setText(getIntent().getStringExtra("ruangan_divisi"));

        loadAsset();
        setupChipFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCabangAktif();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == 100 || requestCode == 101) {
            chipSemua.setChecked(true); // reset filter
            loadAsset();                // reload data dari server
        }
    }

    private void loadCabangAktif() {
        SharedPreferences prefs = getSharedPreferences("pref_selected_cabang", MODE_PRIVATE);
        tvNamaCabang.setText(prefs.getString("nama_cabang", "Belum dipilih"));
        tvAlamatCabang.setText(prefs.getString("alamat_cabang", "-"));
    }

    private void loadAsset() {
        Log.d("ASSET_API_DEBUG", "idDivisi = " + idDivisi);
        String url = Db_Contract.urlGetAsset + "?id_divisi=" + idDivisi;
        Log.d("ASSET_API_DEBUG", "URL = " + url);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        assetList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            assetList.add(new AssetModel(
                                    obj.getString("id_asset"),
                                    obj.getString("nama_asset"),
                                    obj.getString("spesifikasi_asset"),
                                    obj.getString("id_jenis"),
                                    obj.getString("nama_jenis"),
                                    obj.getString("kondisi_asset"),
                                    obj.getString("kendala_asset"),
                                    idDivisi
                            ));
                        }

                        // 🔥 WAJIB
                        assetAdapter.setData(assetList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal load asset", Toast.LENGTH_SHORT).show()
        );

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    // ================= DELETE =================
    private void showDeleteDialog() {
        if (selectedAsset == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Hapus Asset")
                .setMessage("Yakin ingin menghapus asset ini?")
                .setPositiveButton("Ya", (d, w) -> {
                    deleteAsset();
                    icDeleteAsset.setVisibility(View.GONE);
                })
                .setNegativeButton("Tidak", (d, w) -> {
                    icDeleteAsset.setVisibility(View.GONE);
                    selectedAsset = null;
                    assetAdapter.clearSelectedAsset();
                })
                .show();
    }

    private void deleteAsset() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlDeleteAsset,
                r -> {
                    selectedAsset = null;
                    assetAdapter.clearSelectedAsset();
                    icDeleteAsset.setVisibility(View.GONE);
                    loadAsset();
                },
                e -> Toast.makeText(this, "Gagal hapus", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_asset", selectedAsset.getId());
                return p;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    // ================= CHIP FILTER =================
    private void setupChipFilter() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            Chip chip = findViewById(checkedIds.get(0));
            assetAdapter.filterByJenis(chip.getText().toString());
        });
    }


    private void showAddDialog() {
        Intent i = new Intent(this, TambahAssetActivity.class);
        i.putExtra("id_divisi", idDivisi);
        startActivityForResult(i, 100);
    }
}
