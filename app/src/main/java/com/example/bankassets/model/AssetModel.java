package com.example.bankassets.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AssetModel implements Serializable {

    private String idAsset;
    private String nama;
    private String spesifikasi;
    private String idJenis;
    private String namaJenis;
    private String kondisi;
    private String kendala;
    private String pic;
    private String idDivisi;

    // ===== Maintenance =====
    private MaintenanceRecordModel nextMaintenance;
    private ArrayList<MaintenanceRecordModel> riwayatMaintenance;

    public AssetModel(String idAsset, String nama, String spesifikasi,
                      String idJenis, String namaJenis,
                      String kondisi, String kendala, String pic, String idDivisi) {
        this.idAsset = idAsset;
        this.nama = nama;
        this.spesifikasi = spesifikasi;
        this.idJenis = idJenis;
        this.namaJenis = namaJenis;
        this.kondisi = kondisi;
        this.kendala = kendala;
        this.pic = pic;
        this.idDivisi = idDivisi;

        this.riwayatMaintenance = new ArrayList<>();

        this.nextMaintenance = null;
    }

    // ===== GETTER =====
    public String getId() {
        return idAsset;
    }

    public String getNama() {
        return nama;
    }

    public String getSpesifikasi() {
        return spesifikasi;
    }

    public String getIdJenis() { return idJenis; }

    public String getNamaJenis() { return namaJenis; }

    public String getKondisi() {
        return kondisi;
    }

    public String getKendala() {
        return kendala;
    }

    public String getPic() {
        return pic;
    }


    public String getDivisi() {
        return idDivisi;
    }

    // ===== MAINTENANCE =====

    public MaintenanceRecordModel getNextMaintenance() {
        return nextMaintenance;
    }

    public ArrayList<MaintenanceRecordModel> getRiwayatMaintenance() {
        return riwayatMaintenance;
    }

    // ===== SETTER =====
    public void setId(String id) {
        this.idAsset = id;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }
}
