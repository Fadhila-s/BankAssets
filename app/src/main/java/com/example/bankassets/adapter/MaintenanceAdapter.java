package com.example.bankassets.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankassets.R;
import com.example.bankassets.activities.DetailMaintenanceActivity;
import com.example.bankassets.activities.UpdateAssetActivity;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.utils.KondisiAssetColor;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {

    private Context context;
    private List<AssetModel> listAll = new ArrayList<>();      // DATA ASLI
    private List<AssetModel> listDisplay = new ArrayList<>();  // DATA TAMPIL

    private AssetModel selectedAsset = null;

    public MaintenanceAdapter(Context context, ArrayList<AssetModel> data) {
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
        holder.tvKondisi.setText(model.getKondisi());

        // Status color
        KondisiAssetColor.setKondisiColor(holder.tvKondisi, model.getKondisi());

        // Edit kondisi
        holder.ubahKondisiAsset.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateAssetActivity.class);
            intent.putExtra("id_asset", model.getId());
            intent.putExtra("kondisi_asset", model.getKondisi());
            intent.putExtra("kendala_asset", model.getKendala());
            ((Activity) context).startActivityForResult(intent, 101);
        });

        // 🔥 PINDAH HALAMAN SAAT ITEM DIKLIK
        holder.cardRoot.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMaintenanceActivity.class);
            intent.putExtra("asset", model);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listDisplay.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;
        TextView tvNama, tvSpesifikasi, tvKendala, tvKondisi, tvJenis;
        ImageView ubahKondisiAsset;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardRoot);
            tvNama = itemView.findViewById(R.id.etNamaAsset);
            tvSpesifikasi = itemView.findViewById(R.id.etSpesifikasi);
            tvKendala = itemView.findViewById(R.id.etKendala);
            tvKondisi = itemView.findViewById(R.id.etKondisiAsset);
            tvJenis = itemView.findViewById(R.id.tvJenisAsset);
            ubahKondisiAsset = itemView.findViewById(R.id.ubahKondisiAsset);
        }
    }
}
