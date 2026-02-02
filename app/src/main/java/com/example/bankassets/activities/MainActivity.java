package com.example.bankassets.activities;

import android.app.AlertDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;

import com.example.bankassets.R;
import com.example.bankassets.adapter.DivisiAdapter;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.model.DivisiModel;
import com.example.bankassets.utils.AssetNotificationChecker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private RecyclerView rvDivisi;
    private DivisiAdapter divisiAdapter;
    private List<DivisiModel> divisiList = new ArrayList<>();
    ArrayList<AssetModel> allAssetList   = new ArrayList<>();
    ArrayList<AssetModel> assetBaikList  = new ArrayList<>();
    ArrayList<AssetModel> assetRusakList = new ArrayList<>();

    private CardView cardTotalAsset, cardAssetBaik, cardAssetRusak;

    private TextView jumlahTotalAsset, jumlahAssetRusak, jumlahAssetBaik;

    private ImageView icDeleteDivisi;

    private String selectedCabangId = null;
    private DivisiModel selectedDivisi;

    private ImageView icDropdown;
    private TextView tvCabang;

    private TextView tvAlamatCabang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        finishAffinity(); // keluar aplikasi
                    }
                });


        setupBottomNav("home");

        // ================= INIT VIEW =================
        rvDivisi   = findViewById(R.id.rvDivisi);
        FloatingActionButton fab = findViewById(R.id.fab_add);

        icDropdown = findViewById(R.id.icDropdownCabang);
        tvCabang = findViewById(R.id.namaCabang);
        tvAlamatCabang = findViewById(R.id.alamatCabang);

        jumlahTotalAsset = findViewById(R.id.jumlahTotalAsset);
        jumlahAssetRusak = findViewById(R.id.jumlahAssetRusak);
        jumlahAssetBaik = findViewById(R.id.jumlahAssetBaik);

        icDeleteDivisi = findViewById(R.id.icDeleteDivisi);
        icDeleteDivisi.setVisibility(View.GONE);

        // ================= RECYCLER VIEW =================
        rvDivisi.setLayoutManager(new LinearLayoutManager(this));
        divisiAdapter = new DivisiAdapter(this, divisiList);
        rvDivisi.setAdapter(divisiAdapter);

        // LONG CLICK DIVISI
        divisiAdapter.setOnLongClickListener(divisi -> {
            selectedDivisi = divisi;
            divisiAdapter.setSelectedDivisi(divisi);
            icDeleteDivisi.setVisibility(View.VISIBLE);
        });

        icDeleteDivisi.setOnClickListener(v -> showDeleteDialog());

        // ================= FAB (+) =================
        fab.setOnClickListener(v -> showAddDialog());

        // ================= DROPDOWN CABANG =================
        icDropdown.setOnClickListener(v -> showCabangDropdown());

        cardTotalAsset = findViewById(R.id.cardTotalAsset);
        cardTotalAsset.setOnClickListener(v -> {
            Intent i = new Intent(this, TotalAssetActivity.class);
            i.putExtra("list", new ArrayList<>(allAssetList));
            i.putExtra("judul", "Total Asset");
            startActivity(i);
        });

        cardAssetBaik = findViewById(R.id.cardAssetBaik);
        cardAssetBaik.setOnClickListener(v -> {
            Intent i = new Intent(this, AssetBaikActivity.class);
            i.putExtra("list", assetBaikList);
            i.putExtra("judul", "Asset Baik");
            startActivity(i);
        });

        cardAssetRusak = findViewById(R.id.cardAssetRusak);
        cardAssetRusak.setOnClickListener(v -> {
            Intent i = new Intent(this, AssetRusakActivity.class);
            i.putExtra("list", assetRusakList);
            i.putExtra("judul", "Asset Rusak");
            startActivity(i);
        });

        cardTotalAsset.setEnabled(false);
        cardAssetBaik.setEnabled(false);
        cardAssetRusak.setEnabled(false);

        loadCabang(); // ⬅️ START POINT
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadCabang(); // reload dari DB
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {
            loadDivisi(selectedCabangId);
        }
    }


    // ================= LOAD CABANG =================
    private void loadCabang() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Db_Contract.urlGetCabang,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        if (array.length() > 0) {
                            JSONObject cabang = array.getJSONObject(0);
                            selectedCabangId = cabang.getString("id_cabang");
                            tvCabang.setText(cabang.getString("nama_cabang"));
                            tvAlamatCabang.setText(cabang.getString("alamat_cabang"));
                            loadDivisi(selectedCabangId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal load cabang", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ================= LOAD DIVISI =================
    private void loadDivisi(String idCabang) {
        divisiList.clear();

        String url = Db_Contract.urlGetDivisi + "?id_cabang=" + idCabang;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            divisiList.add(new DivisiModel(
                                    obj.getString("id_divisi"),
                                    obj.getString("nama_divisi"),
                                    obj.getString("ruangan_divisi")
                            ));
                        }
                        divisiAdapter.notifyDataSetChanged();
                        loadAssetCabang(idCabang);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal load divisi", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadAssetCabang(String idCabang) {
        String url = Db_Contract.urlGetAssetCabang + "?id_cabang=" + idCabang;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        allAssetList.clear();
                        assetBaikList.clear();
                        assetRusakList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            AssetModel asset = new AssetModel(
                                    obj.getString("id_asset"),
                                    obj.getString("nama_asset"),
                                    obj.getString("spesifikasi_asset"),
                                    obj.getString("id_jenis"),
                                    obj.getString("nama_jenis"),
                                    obj.getString("kondisi_asset"),
                                    obj.getString("kendala_asset"),
                                    idCabang
                            );

                            allAssetList.add(asset);

                            if (asset.getKondisi().equalsIgnoreCase("Baik")) {
                                assetBaikList.add(asset);
                            } else if (
                                    asset.getKondisi().equalsIgnoreCase("Kritis") ||
                                            asset.getKondisi().equalsIgnoreCase("Peringatan")
                            ) {
                                assetRusakList.add(asset);
                            }
                        }

                        // ===== SET ANGKA =====
                        jumlahTotalAsset.setText(String.valueOf(allAssetList.size()));
                        jumlahAssetBaik.setText(String.valueOf(assetBaikList.size()));
                        jumlahAssetRusak.setText(String.valueOf(assetRusakList.size()));

                        // 🔔 PANGGIL INI
                        AssetNotificationChecker.checkAndNotify(
                                this,
                                allAssetList
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cardTotalAsset.setEnabled(true);
                    cardAssetBaik.setEnabled(true);
                    cardAssetRusak.setEnabled(true);
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ================= DROPDOWN CABANG =================
    private void showCabangDropdown() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Db_Contract.urlGetCabang,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        String[] items = new String[array.length()];

                        for (int i = 0; i < array.length(); i++) {
                            items[i] = array.getJSONObject(i).getString("nama_cabang");
                        }

                        new AlertDialog.Builder(this)
                                .setTitle("Pilih Cabang")
                                .setItems(items, (d, position) -> {
                                    try {
                                        JSONObject obj = array.getJSONObject(position);

                                        selectedCabangId = obj.getString("id_cabang");
                                        String nama = obj.getString("nama_cabang");
                                        String alamat = obj.getString("alamat_cabang");

                                        tvCabang.setText(nama);
                                        tvAlamatCabang.setText(alamat);

                                        saveSelectedCabang(selectedCabangId, nama, alamat);
                                        loadDivisi(selectedCabangId);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal ambil cabang", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ================= DELETE DIVISI =================
    private void showDeleteDialog() {
        if (selectedDivisi == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Hapus Divisi")
                .setMessage("Yakin ingin menghapus divisi ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    deleteDivisi();

                    // HILANGKAN ICON MESKI YES
                    icDeleteDivisi.setVisibility(View.GONE);
                    selectedDivisi = null;
                    divisiAdapter.clearSelectedDivisi();

                    dialog.dismiss();
                })
                .setNegativeButton("Tidak", (dialog, which) -> {
                    // HILANGKAN ICON MESKI NO
                    icDeleteDivisi.setVisibility(View.GONE);
                    selectedDivisi = null;
                    divisiAdapter.clearSelectedDivisi();

                    dialog.dismiss();
                })
                .show();
    }


    private void deleteDivisi() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlDeleteDivisi,
                response -> {
                    Toast.makeText(this, "Divisi dihapus", Toast.LENGTH_SHORT).show();
                    selectedDivisi = null;
                    divisiAdapter.clearSelectedDivisi();
                    loadDivisi(selectedCabangId);
                },
                error -> Toast.makeText(this, "Gagal hapus", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_divisi", selectedDivisi.getIdDivisi());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ================= DIALOG FAB =================
    private void showAddDialog() {
        String[] options = {"Tambah Cabang", "Tambah Divisi"};

        new AlertDialog.Builder(this)
                .setTitle("Pilih Aksi")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Tambah Cabang
                        startActivityForResult(
                                new Intent(this, TambahCabangActivity.class),
                                100
                        );
                    } else {
                        // Tambah Divisi
                        if (selectedCabangId == null) {
                            Toast.makeText(this, "Pilih cabang dulu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent i = new Intent(this, TambahDivisiActivity.class);
                        i.putExtra("id_cabang", selectedCabangId);
                        startActivityForResult(i, 200);
                    }
                })
                .show();
    }

    private void saveSelectedCabang(String id, String nama, String alamat) {
        getSharedPreferences("pref_selected_cabang", MODE_PRIVATE)
                .edit()
                .putString("id_cabang", id)
                .putString("nama_cabang", nama)
                .putString("alamat_cabang", alamat)
                .apply();
    }

}
