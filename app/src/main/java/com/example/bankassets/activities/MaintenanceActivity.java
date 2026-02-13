package com.example.bankassets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.example.bankassets.adapter.AssetAdapter;
import com.example.bankassets.adapter.MaintenanceAdapter;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.utils.AssetNotificationChecker;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenanceActivity extends BaseActivity {

    private EditText etSearch;
    private LinearLayout containerSearch;
    private TextView tvNamaDivisi, tvRuanganDivisi;
    private ImageView icDropdownDivisi;
    private RecyclerView rvAsset;

    private ArrayList<AssetModel> assetList;
    private MaintenanceAdapter adapter;

    private boolean isSearchActive = false;

    private String selectedIdDivisi = "";
    private String idCabangAktif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        setupBottomNav("maintenance");

        tvNamaDivisi = findViewById(R.id.tvNamaDivisi);
        tvRuanganDivisi = findViewById(R.id.tvRuanganDivisi);
        icDropdownDivisi = findViewById(R.id.icDropdownDivisi);
        rvAsset = findViewById(R.id.rvAsset);

        etSearch = findViewById(R.id.etSearch);
        containerSearch = findViewById(R.id.containerSearch);

        // ===== RecyclerView =====
        assetList = new ArrayList<>();
        adapter = new MaintenanceAdapter(this, assetList);
        rvAsset.setLayoutManager(new LinearLayoutManager(this));
        rvAsset.setAdapter(adapter);

        // 🔥 ambil cabang aktif (SAMA KAYAK AssetRuangan)
        idCabangAktif = getSharedPreferences(
                "pref_selected_cabang", MODE_PRIVATE
        ).getString("id_cabang", "");

        if (idCabangAktif.isEmpty()) {
            Toast.makeText(this, "Cabang belum dipilih", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        loadDivisiDefault();

        icDropdownDivisi.setOnClickListener(v -> showDivisiDropdown());

        containerSearch.setOnClickListener(v -> activateSearch());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterByPic(s.toString());
                adapter.setSearchKeyword(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {

                // Tutup keyboard
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }

                if (adapter.getItemCount() == 0) {
                    Toast.makeText(this,
                            "Tidak ada asset yang sesuai dengan PIC",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        if (isSearchActive) {
            isSearchActive = false;

            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }

            etSearch.clearFocus();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(MaintenanceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            loadAsset();
        }
    }

    private void loadDivisiDefault() {
        String url = Db_Contract.urlGetDivisi + "?id_cabang=" + idCabangAktif;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        if (array.length() > 0) {
                            JSONObject obj = array.getJSONObject(0);

                            selectedIdDivisi = obj.getString("id_divisi");
                            tvNamaDivisi.setText(obj.getString("nama_divisi"));
                            tvRuanganDivisi.setText(obj.getString("ruangan_divisi"));

                            loadAsset(); // 🔥 PERTAMA KALI LOAD
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ===== DROPDOWN =====
    private void showDivisiDropdown() {
        String url = Db_Contract.urlGetDivisi + "?id_cabang=" + idCabangAktif;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() == 0) {
                            Toast.makeText(this, "Tidak ada divisi", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String[] items = new String[array.length()];

                        for (int i = 0; i < array.length(); i++) {
                            items[i] = array.getJSONObject(i)
                                    .getString("nama_divisi");
                        }

                        new AlertDialog.Builder(this)
                                .setTitle("Pilih Divisi")
                                .setItems(items, (dialog, which) -> {
                                    try {
                                        JSONObject obj = array.getJSONObject(which);

                                        selectedIdDivisi = obj.getString("id_divisi");
                                        tvNamaDivisi.setText(obj.getString("nama_divisi"));
                                        tvRuanganDivisi.setText(obj.getString("ruangan_divisi"));

                                        loadAsset(); // 🔥 LOAD ASSET SESUAI DIVISI
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                                .show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal load divisi", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void activateSearch() {
        if (isSearchActive) return;

        isSearchActive = true;
        etSearch.setEnabled(true);
        etSearch.setFocusableInTouchMode(true);
        etSearch.requestFocus();

        // ⌨️ Paksa keyboard muncul
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // ===== LOAD ASSET =====
    private void loadAsset() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlGetMaintenanceAsset,
                response -> {
                    try {
                        Log.d("ASSET_API_DEBUG", "RESPONSE = " + response);

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
                                    obj.getString("status_penggunaan"),
                                    obj.getString("pic_asset"),
                                    selectedIdDivisi
                            ));
                        }

                        // 🔥 WAJIB
                        adapter.setData(assetList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal load asset", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_divisi", selectedIdDivisi);
                return params;
                }
        };

        Volley.newRequestQueue(this).add(request);
    }

}

