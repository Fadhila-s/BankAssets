package com.example.bankassets.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bankassets.R;
import com.example.bankassets.model.MaintenanceRecordModel;
import com.example.bankassets.Db_Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaintenanceRecordAdapter
        extends RecyclerView.Adapter<MaintenanceRecordAdapter.ViewHolder> {

    private Context context;
    private MaintenanceRecordModel selectedMaintenance = null;
    private List<MaintenanceRecordModel> listDisplay = new ArrayList<>();

    public MaintenanceRecordAdapter(Context context, ArrayList<MaintenanceRecordModel> data) {
        this.context = context;
        setData(data);
    }

    // ================= SET DATA =================
    public void setData(List<MaintenanceRecordModel> data) {
        listDisplay.clear();
        listDisplay.addAll(data);

        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;

        TextView tvTanggal, tvAktivitas, tvCatatan, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardRoot);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvAktivitas   = itemView.findViewById(R.id.tvAktivitas);
            tvCatatan = itemView.findViewById(R.id.tvCatatan);
            tvStatus  = itemView.findViewById(R.id.tvStatus);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.riwayat_maintenance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        MaintenanceRecordModel item = listDisplay.get(position);

        holder.tvTanggal.setText(item.getTanggal());
        holder.tvAktivitas.setText(item.getAktivitas_maintenance());
        holder.tvCatatan.setText(item.getCatatan());

        setRiwayatMaintenance(holder.tvStatus, item.getStatus());

        // 👇 STATUS BISA DIKLIK
        holder.tvStatus.setOnClickListener(v -> {
            showStatusPickerDialog(item, holder.tvStatus);
        });

        // ===== LONG CLICK (SELECT) =====
        holder.itemView.setOnLongClickListener(v -> {
            selectedMaintenance= item;
            if (listener != null) listener.onLongClick(item);
            notifyDataSetChanged();
            return true;
        });

        // ===== HIGHLIGHT =====
        if (selectedMaintenance != null
                && selectedMaintenance.getId_maintenance() != null
                && selectedMaintenance.getId_maintenance().equals(item.getId_maintenance())) {

            holder.cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface)
            );
        } else {
            holder.cardRoot.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.bg_card)
            );
        }

    }

    private void showStatusPickerDialog(
            MaintenanceRecordModel item,
            TextView tvStatus
    ) {

        String[] labels = {
                "✔️ Maintenance berhasil",
                "✖️ Maintenance gagal",
                "➖ Belum dicek"
        };

        String[] values = {"v", "x", "-"};

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Pilih status maintenance")
                .setItems(labels, (dialog, which) -> {

                    String selectedStatus = values[which];

                    showConfirmDialog(item, tvStatus, selectedStatus);

                })
                .setCancelable(true) // 🔥 klik luar = hilang
                .show();
    }

    private void showConfirmDialog(
            MaintenanceRecordModel item,
            TextView tvStatus,
            String newStatus
    ) {

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setMessage("Yakin ingin mengganti status maintenance?")
                .setPositiveButton("Ya", (d, w) -> {

                    // 🔥 UPDATE MODEL
                    item.setStatus(newStatus);

                    // 🔥 UPDATE UI
                    setRiwayatMaintenance(tvStatus, newStatus);

                    // 🔥 UPDATE DB
                    updateStatusToServer(item);

                    Toast.makeText(
                            context,
                            newStatus.equals("v") ? "Maintenance berhasil"
                                    : newStatus.equals("x") ? "Maintenance gagal"
                                    : "Status direset",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Tidak", null) // 🔥 otomatis close
                .show();
    }


    private void updateStatusToServer(MaintenanceRecordModel item) {
        Log.d("STATUS_API", "ID = " + item.getId_maintenance());
        Log.d("STATUS_API", "STATUS = " + item.getStatus());
        Log.d("STATUS_API", "URL = " + Db_Contract.urlUpdateStatusMaintenance);

        StringRequest req = new StringRequest(
                Request.Method.POST,
                Db_Contract.urlUpdateStatusMaintenance,
                r -> Log.d("STATUS_API", "OK"),
                e -> Toast.makeText(context, "Gagal update status", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_maintenance", item.getId_maintenance());
                p.put("status", item.getStatus());
                return p;
            }
        };

        Volley.newRequestQueue(context).add(req);
    }

    @Override
    public int getItemCount() {
        return listDisplay.size();
    }


    private void setRiwayatMaintenance(TextView tv, String status) {

        switch (status) {
            case "-":
                tv.setText("➖");
                tv.setBackgroundResource(R.drawable.bg_square_grey);
                tv.setTextColor(Color.parseColor("#616161"));
                break;

            case "v":
                tv.setText("✔️");
                tv.setBackgroundResource(R.drawable.bg_status_green);
                tv.setTextColor(Color.parseColor("#2E7D32"));
                break;

            case "x":
                tv.setText("✖️");
                tv.setBackgroundResource(R.drawable.bg_status_red);
                tv.setTextColor(Color.parseColor("#D32F2F"));
                break;

            default:
                tv.setText("➖");
                tv.setBackgroundResource(R.drawable.bg_square_grey);
                tv.setTextColor(Color.GRAY);
                break;
        }
    }

    // ================= SELECT =================
    public void setSelectedMaintenance(MaintenanceRecordModel maintenance) {
        selectedMaintenance = maintenance;
        notifyDataSetChanged();
    }

    public void clearSelectedMaintenance() {
        selectedMaintenance = null;
        notifyDataSetChanged();
    }

    // ================= LONG CLICK LISTENER =================
    public interface OnMaintenanceLongClick {
        void onLongClick(MaintenanceRecordModel maintenance);
    }

    private OnMaintenanceLongClick listener;

    public void setOnLongClickListener(MaintenanceRecordAdapter.OnMaintenanceLongClick l) {
        listener = l;
    }
}
