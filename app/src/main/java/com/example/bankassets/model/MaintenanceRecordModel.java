package com.example.bankassets.model;

import java.io.Serializable;

public class MaintenanceRecordModel implements Serializable {

    private String id_maintenance;
    private String tanggal;
    private String aktivitas_maintenance;
    private String catatan;
    private String status;

    public MaintenanceRecordModel(String id_maintenance, String tanggal, String aktivitas_maintenance,
                                  String catatan, String status) {
        this.id_maintenance = id_maintenance;
        this.tanggal = tanggal;
        this.aktivitas_maintenance = aktivitas_maintenance;
        this.catatan = catatan;
        this.status = status;
    }

    public String getId_maintenance() {
        return id_maintenance;
    }
    public String getTanggal() {
        return tanggal;
    }

    public String getAktivitas_maintenance() {
        return aktivitas_maintenance;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
