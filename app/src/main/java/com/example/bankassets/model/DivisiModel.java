package com.example.bankassets.model;

public class DivisiModel {

    private String idDivisi;
    private String namaDivisi;
    private String ruanganDivisi;

    public DivisiModel(String idDivisi, String namaDivisi, String ruanganDivisi) {
        this.idDivisi = idDivisi;
        this.namaDivisi = namaDivisi;
        this.ruanganDivisi = ruanganDivisi;
    }

    public String getIdDivisi() {
        return idDivisi;
    }

    public String getNamaDivisi() {
        return namaDivisi;
    }

    public String getRuanganDivisi() {
        return ruanganDivisi;
    }

    // ================= SETTER (OPSIONAL) =================
    public void setIdDivisi(String idDivisi) {
        this.idDivisi = idDivisi;
    }

    public void setNamaDivisi(String namaDivisi) {
        this.namaDivisi = namaDivisi;
    }

    public void setRuanganDivisi(String ruanganDivisi) {
        this.ruanganDivisi = ruanganDivisi;
    }
}
