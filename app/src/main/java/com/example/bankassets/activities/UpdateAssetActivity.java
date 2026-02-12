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

public class UpdateAssetActivity extends BaseActivity {

    private TextInputEditText etKendala;
    private MaterialAutoCompleteTextView ddKondisiAsset;
    private TextInputEditText etPic;

    private MaterialButton btnUpdate;

    private ImageView icDropdownKondisi;

    private String idAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_asset);

        // ===== Toolbar =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etKendala     = findViewById(R.id.etKendala);
        etPic         = findViewById(R.id.etPic);
        ddKondisiAsset = findViewById(R.id.etKondisiAsset);

        btnUpdate = findViewById(R.id.btnSave);
        icDropdownKondisi = findViewById(R.id.icDropdownKondisi);

        idAsset = getIntent().getStringExtra("id_asset");

        if (idAsset == null || idAsset.isEmpty()) {
            Toast.makeText(this, "ID Asset tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etKendala.setText(getIntent().getStringExtra("kendala"));
        etPic.setText(getIntent().getStringExtra("pic"));
        ddKondisiAsset.setText(
                getIntent().getStringExtra("kondisi_asset"),
                false
        );

        setupDropdown();
        // ✅ KLIK ICON UNTUK BUKA DROPDOWN
        icDropdownKondisi.setOnClickListener(v -> ddKondisiAsset.showDropDown());

        btnUpdate.setOnClickListener(v -> updateAsset());
    }

    private void setupDropdown() {
        // ===== Kondisi Asset =====
        String[] kondisiAsset = {"Baik", "Peringatan", "Kritis"};

        ddKondisiAsset.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        kondisiAsset)
        );
    }

    private void updateAsset() {
        if (ddKondisiAsset.getText().toString().isEmpty()) {
            Toast.makeText(this, "Pilih kondisi asset", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlUpdateAsset,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equals("success")) {
                            Toast.makeText(this, "Asset diperbarui", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_asset", idAsset);
                params.put("kondisi_asset", ddKondisiAsset.getText().toString());
                params.put("kendala_asset", etKendala.getText().toString());
                params.put("pic_asset", etPic.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
