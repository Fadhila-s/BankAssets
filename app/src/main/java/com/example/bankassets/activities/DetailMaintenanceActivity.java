package com.example.bankassets.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.Db_Contract;
import com.example.bankassets.R;
import com.example.bankassets.adapter.MaintenanceRecordAdapter;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.model.MaintenanceRecordModel;
import com.example.bankassets.utils.KondisiAssetColor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailMaintenanceActivity extends BaseActivity {

    private ImageView icDeleteMaintenance;
    private FloatingActionButton fabAdd;
    private MaintenanceRecordModel selectedMaintenance;

    private AssetModel asset;
    private ArrayList<MaintenanceRecordModel> riwayatMaintenance;
    private MaintenanceRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_maintenance);

        TextView tvNamaAsset = findViewById(R.id.tvNamaAsset);
        TextView tvSpesifikasi = findViewById(R.id.tvSpesifikasiAsset);
        TextView tvKondisi = findViewById(R.id.etKondisiAsset);
        TextView tvKendala = findViewById(R.id.etKendala);
        fabAdd = findViewById(R.id.fabAddMaintenance);
        icDeleteMaintenance = findViewById(R.id.icDeleteMaintenance);
        icDeleteMaintenance.setVisibility(View.GONE);

        asset = (AssetModel) getIntent().getSerializableExtra("asset");

        if (asset == null) {
            Toast.makeText(this, "Asset tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ===== SET DATA ASSET =====
        tvNamaAsset.setText(asset.getNama());
        tvSpesifikasi.setText(asset.getSpesifikasi());
        tvKondisi.setText(asset.getKondisi());
        KondisiAssetColor.setKondisiColor(tvKondisi, asset.getKondisi());
        tvKendala.setText(asset.getKendala());

        RecyclerView rv = findViewById(R.id.rvMaintenance);
        rv.setLayoutManager(new LinearLayoutManager(this));

        riwayatMaintenance = new ArrayList<>();
        adapter = new MaintenanceRecordAdapter(this, riwayatMaintenance);
        rv.setAdapter(adapter);

        loadMaintenance();
        // ===== FAB =====
        fabAdd.setOnClickListener(v -> showAddDialog());

        // LONG CLICK
        adapter.setOnLongClickListener(maintenance -> {
            selectedMaintenance = maintenance;
            adapter.setSelectedMaintenance(maintenance);
            icDeleteMaintenance.setVisibility(View.VISIBLE);
        });

        icDeleteMaintenance.setOnClickListener(v -> showDeleteDialog());

    }

    // ================= LOAD =================
    private void loadMaintenance() {
        String url = Db_Contract.urlGetMaintenanceDetail +
                "?id_asset=" + asset.getId();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        riwayatMaintenance.clear();
                        MaintenanceRecordModel maintenanceBerikutnya = null;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);

                            MaintenanceRecordModel m = new MaintenanceRecordModel(
                                    o.getString("id_maintenance"),
                                    o.getString("tanggal"),
                                    o.getString("aktivitas_maintenance"),
                                    o.getString("catatan"),
                                    o.getString("status")
                            );

                            // 🔥 SEMUA MASUK LIST
                            riwayatMaintenance.add(m);
                        }

                        adapter.setData(riwayatMaintenance);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this,
                        "Gagal load maintenance",
                        Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }


    // ================= DELETE =================
    private void showDeleteDialog() {
        if (selectedMaintenance == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Hapus Maintenance")
                .setMessage("Yakin ingin menghapus?")
                .setPositiveButton("Ya", (d, w) -> {
                    deleteMaintenance();
                    icDeleteMaintenance.setVisibility(View.GONE);
                })
                .setNegativeButton("Tidak", (d, w) -> {
                    icDeleteMaintenance.setVisibility(View.GONE);
                    selectedMaintenance = null;
                    adapter.clearSelectedMaintenance();
                })
                .show();
    }

    private void deleteMaintenance() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlDeleteMaintenance,
                r -> loadMaintenance(),
                e -> Toast.makeText(this, "Gagal hapus", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_maintenance", selectedMaintenance.getId_maintenance());
                return p;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void showAddDialog() {
        Intent i = new Intent(this, TambahMaintenanceActivity.class);
        i.putExtra("id_asset", asset.getId());
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadMaintenance(); // 🔥 REFRESH RIWAYAT
        }
    }
}
