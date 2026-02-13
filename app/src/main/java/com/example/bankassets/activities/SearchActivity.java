package com.example.bankassets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.example.bankassets.adapter.AssetAdapter;
import com.example.bankassets.model.AssetModel;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private EditText etSearch;
    private TextView tvNamaDivisi, tvRuanganDivisi;
    private ImageView icDropdownDivisi;
    private LinearLayout containerSearch;
    private RecyclerView rvAsset;

    private AssetAdapter assetAdapter;
    private List<AssetModel> assetList = new ArrayList<>();

    private boolean isSearchActive = false;
    private String selectedIdDivisi = "";
    private String idCabangAktif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupBottomNav("search");

        tvNamaDivisi = findViewById(R.id.tvNamaDivisi);
        tvRuanganDivisi = findViewById(R.id.tvRuanganDivisi);
        icDropdownDivisi = findViewById(R.id.icDropdownDivisi);

        etSearch = findViewById(R.id.etSearch);
        containerSearch = findViewById(R.id.search);
        rvAsset = findViewById(R.id.rvAsset);

        rvAsset.setLayoutManager(new LinearLayoutManager(this));
        assetAdapter = new AssetAdapter(this, new ArrayList<>());
        rvAsset.setAdapter(assetAdapter);

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
                assetAdapter.filterByNama(s.toString());
                assetAdapter.setSearchKeyword(s.toString());
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

                if (assetAdapter.getItemCount() == 0) {
                    Toast.makeText(this,
                            "Tidak ada asset yang sesuai",
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
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
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


    private void loadAsset() {
        Log.d("SEARCH", "loadAsset() DIPANGGIL, id_divisi=" + selectedIdDivisi);

        assetList.clear();
        assetAdapter.notifyDataSetChanged();

        String url = Db_Contract.urlGetAsset + "?id_divisi=" + selectedIdDivisi;

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
                                    obj.getString("status_penggunaan"),
                                    obj.getString("pic_asset"),
                                    selectedIdDivisi
                            ));
                        }
                        assetAdapter.setData(assetList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );
        Volley.newRequestQueue(this).add(request);
    }
}

