package com.example.bankassets.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankassets.R;
import com.example.bankassets.activities.UpdateAssetActivity;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.model.DivisiModel;
import com.example.bankassets.utils.KondisiAssetColor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {

    private Context context;
    private List<AssetModel> listAll = new ArrayList<>();      // DATA ASLI
    private List<AssetModel> listDisplay = new ArrayList<>();  // DATA TAMPIL

    // 🔥 SINGLE SELECT
    private AssetModel selectedAsset = null;
    private String searchKeyword = "";

    // ================= CONSTRUCTOR =================
    public AssetAdapter(Context context, List<AssetModel> data) {
        this.context = context;
        setData(data);
    }

    // ================= SET DATA =================
    public void setData(List<AssetModel> data) {
        listAll.clear();
        listAll.addAll(data);

        listDisplay.clear();
        listDisplay.addAll(data);

        notifyDataSetChanged();
    }

    // ================= FILTER =================
    public void filterByJenis(String jenis) {
        listDisplay.clear();

        Log.d("FILTER_DEBUG",
                "Filter: '" + jenis + "' | totalAll=" + listAll.size());

        if (jenis == null || jenis.trim().equalsIgnoreCase("Semua")) {
            listDisplay.addAll(listAll);
        } else {
            for (AssetModel a : listAll) {
                if (a.getNamaJenis() != null &&
                        a.getNamaJenis().toLowerCase().trim()
                                .contains(jenis.toLowerCase().trim())) {

                    listDisplay.add(a);
                }
            }
        }

        Log.d("FILTER_DEBUG", "Hasil tampil = " + listDisplay.size());
        notifyDataSetChanged();
    }


    // ================= SELECT =================
    public void setSelectedAsset(AssetModel asset) {
        selectedAsset = asset;
        notifyDataSetChanged();
    }

    public void clearSelectedAsset() {
        selectedAsset = null;
        notifyDataSetChanged();
    }

    // ================= LONG CLICK LISTENER =================
    public interface OnAssetLongClick {
        void onLongClick(AssetModel asset);
    }

    private OnAssetLongClick listener;

    public void setOnLongClickListener(OnAssetLongClick l) {
        listener = l;
    }

    @Override
    public int getItemCount() {
        return listDisplay.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssetModel model = listDisplay.get(position);

        holder.tvNama.setText(model.getNama());
        holder.tvJenis.setText(model.getNamaJenis());
        holder.tvSpesifikasi.setText(model.getSpesifikasi());
        holder.tvKendala.setText(model.getKendala());
        holder.tvPic.setText(model.getPic());
        holder.tvKondisi.setText(model.getKondisi());

        // Status color
        KondisiAssetColor.setKondisiColor(holder.tvKondisi, model.getKondisi());

        // Edit kondisi
        holder.ubahKondisiAsset.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateAssetActivity.class);
            intent.putExtra("id_asset", model.getId());
            intent.putExtra("kondisi_asset", model.getKondisi());
            intent.putExtra("kendala_asset", model.getKendala());
            intent.putExtra("pic_asset", model.getPic());
            ((Activity) context).startActivityForResult(intent, 101);
        });


        // ===== LONG CLICK (SELECT) =====
        holder.itemView.setOnLongClickListener(v -> {
            selectedAsset = model;
            if (listener != null) listener.onLongClick(model);
            notifyDataSetChanged();
            return true;
        });

        // ===== HIGHLIGHT =====
        if (selectedAsset != null
                && selectedAsset.getId().equals(model.getId())) {
            holder.cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface)
            );
        } else {
            holder.cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.bg_card)
            );
        }

        String nama = model.getNama();

        if (!searchKeyword.isEmpty() && nama.toLowerCase().contains(searchKeyword)) {

            SpannableString spannable = new SpannableString(nama);
            int start = nama.toLowerCase().indexOf(searchKeyword);
            int end = start + searchKeyword.length();

            spannable.setSpan(
                    new ForegroundColorSpan(
                            ContextCompat.getColor(context, R.color.primaryBlue)
                    ),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            holder.tvNama.setText(spannable);

        } else {
            holder.tvNama.setText(nama);
        }
    }

    public void setSearchKeyword(String keyword) {
        this.searchKeyword = keyword == null ? "" : keyword.toLowerCase().trim();
        notifyDataSetChanged();
    }

    public void filterByNama(String keyword) {
        searchKeyword = keyword == null ? "" : keyword.toLowerCase().trim();
        listDisplay.clear();

        if (searchKeyword.isEmpty()) {
            listDisplay.addAll(listAll);
        } else {
            for (AssetModel a : listAll) {
                if (a.getNama() != null &&
                        a.getNama().toLowerCase().contains(searchKeyword)) {
                    listDisplay.add(a);
                }
            }
        }

        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;
        TextView tvNama, tvSpesifikasi, tvKendala, tvPic, tvKondisi, tvJenis;
        ImageView ubahKondisiAsset;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardRoot);
            tvNama = itemView.findViewById(R.id.etNamaAsset);
            tvSpesifikasi = itemView.findViewById(R.id.etSpesifikasi);
            tvKendala = itemView.findViewById(R.id.etKendala);
            tvPic = itemView.findViewById(R.id.etPic);
            tvKondisi = itemView.findViewById(R.id.etKondisiAsset);
            tvJenis = itemView.findViewById(R.id.tvJenisAsset);
            ubahKondisiAsset = itemView.findViewById(R.id.ubahKondisiAsset);
        }
    }
}
