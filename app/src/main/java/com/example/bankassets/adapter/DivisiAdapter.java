package com.example.bankassets.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankassets.R;
import com.example.bankassets.activities.AssetRuangan;
import com.example.bankassets.model.AssetModel;
import com.example.bankassets.model.DivisiModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DivisiAdapter extends RecyclerView.Adapter<DivisiAdapter.ViewHolder> {

    private Context context;
    private List<DivisiModel> divisiList;

    // 🔥 SINGLE SELECT
    private DivisiModel selectedDivisi = null;

    public DivisiAdapter(Context context, List<DivisiModel> divisiList) {
        this.context = context;
        this.divisiList = divisiList;
    }

    // ================= LONG CLICK LISTENER =================
    public interface OnDivisiLongClick {
        void onLongClick(DivisiModel divisi);
    }

    private OnDivisiLongClick listener;

    public void setOnLongClickListener(OnDivisiLongClick listener) {
        this.listener = listener;
    }
    // ================= SELECTED DIVISI =================
    public void setSelectedDivisi(DivisiModel divisi) {
        selectedDivisi = divisi;
        notifyDataSetChanged();
    }

    public void clearSelectedDivisi() {
        selectedDivisi = null;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_divisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DivisiModel model = divisiList.get(position);

        holder.tvNama.setText(model.getNamaDivisi());
        holder.tvRuangan.setText(model.getRuanganDivisi());

        // ===== CLICK (BUKA ASSET) =====
        holder.itemView.setOnClickListener(v -> {
            if (selectedDivisi != null) {
                // jika lagi mode select, abaikan click
                return;
            }
            Intent i = new Intent(context, AssetRuangan.class);
            i.putExtra("id_divisi", model.getIdDivisi());
            i.putExtra("nama_divisi", model.getNamaDivisi());
            i.putExtra("ruangan_divisi", model.getRuanganDivisi());
            context.startActivity(i);
        });

        // ===== LONG CLICK (SELECT) =====
        holder.itemView.setOnLongClickListener(v -> {
            selectedDivisi = model;
            if (listener != null) {
                listener.onLongClick(model);
            }
            notifyDataSetChanged();
            return true;
        });

        // ===== HIGHLIGHT =====
        if (selectedDivisi != null
                && selectedDivisi.getIdDivisi().equals(model.getIdDivisi())) {
            holder.cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface)
            );
        } else {
            holder.cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.bg_card)
            );
        }
    }

    @Override
    public int getItemCount() {
        return divisiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;
        TextView tvNama, tvRuangan;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardRoot = itemView.findViewById(R.id.cardRoot);
            tvNama = itemView.findViewById(R.id.tvNamaDivisi);
            tvRuangan = itemView.findViewById(R.id.tvRuanganDivisi);
        }
    }
}

