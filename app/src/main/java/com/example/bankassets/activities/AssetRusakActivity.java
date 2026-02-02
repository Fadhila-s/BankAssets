package com.example.bankassets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankassets.R;
import com.example.bankassets.adapter.AssetAdapter;
import com.example.bankassets.model.AssetModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;

public class AssetRusakActivity extends BaseActivity {

    private RecyclerView rvAsset;
    private AssetAdapter assetAdapter;
    private TextView tvNamaCabang, tvAlamatCabang;
    private ArrayList<AssetModel> assetList = new ArrayList<>();
    private ArrayList<AssetModel> filteredList = new ArrayList<>();

    private TextView jumlahAssetRusak;
    private ChipGroup chipGroup;
    private Chip chipSemua; // ✅ WAJIB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asset_rusak);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAsset = findViewById(R.id.rvAsset);
        tvAlamatCabang = findViewById(R.id.alamatCabang);
        tvNamaCabang   = findViewById(R.id.namaCabang);
        jumlahAssetRusak = findViewById(R.id.jumlahAssetRusak);
        chipGroup = findViewById(R.id.chipGroup);
        chipSemua = findViewById(R.id.chipSemua);

        // DEFAULT FILTER = SEMUA
        chipSemua.setChecked(true);

        assetList = (ArrayList<AssetModel>)
                getIntent().getSerializableExtra("list");

        if (assetList == null) assetList = new ArrayList<>();

        jumlahAssetRusak.setText(String.valueOf(assetList.size()));

        rvAsset.setLayoutManager(new LinearLayoutManager(this));
        assetAdapter = new AssetAdapter(this, assetList);
        rvAsset.setAdapter(assetAdapter);

        showAllAsset();
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

        if (requestCode == 101 && resultCode == RESULT_OK) {
            setResult(RESULT_OK); // kirim sinyal ke activity sebelumnya
            finish();             // balik & reload di sana
        }
    }

    private void loadCabangAktif() {
        SharedPreferences prefs = getSharedPreferences("pref_selected_cabang", MODE_PRIVATE);
        tvNamaCabang.setText(prefs.getString("nama_cabang", "Belum dipilih"));
        tvAlamatCabang.setText(prefs.getString("alamat_cabang", "-"));
    }

    // =========================
    // SHOW ALL
    // =========================
    private void showAllAsset() {
        filteredList.clear();
        filteredList.addAll(assetList);
        assetAdapter.notifyDataSetChanged();
        jumlahAssetRusak.setText(String.valueOf(filteredList.size()));
    }

    private void filterByKategori(String kategori) {
        filteredList.clear();

        for (AssetModel asset : assetList) {
            if (asset.getNamaJenis().equalsIgnoreCase(kategori)) {
                filteredList.add(asset);
            }
        }

        assetAdapter.setData(filteredList);
        jumlahAssetRusak.setText(String.valueOf(filteredList.size()));
    }

    // ================= CHIP FILTER =================
    private void setupChipFilter() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            Chip chip = findViewById(checkedIds.get(0));
            String kategori = chip.getText().toString();

            if (kategori.equalsIgnoreCase("Semua")) {
                showAllAsset();
            } else {
                filterByKategori(kategori);
            }
        });
    }
}
