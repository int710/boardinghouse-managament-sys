package com.ptpmud.quanlynhatro.model;

public class DichVu {
    private int idDichVu;
    private String tenDichVu;
    private double donGia;
    private String moTa;
    private String ngayTao;

    public DichVu() {
    }

    public DichVu(int idDichVu, String tenDichVu, double donGia, String moTa, String ngayTao) {
        this.idDichVu = idDichVu;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
        this.moTa = moTa;
        this.ngayTao = ngayTao;
    }

    public int getIdDichVu() {
        return idDichVu;
    }

    public void setIdDichVu(int idDichVu) {
        this.idDichVu = idDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return tenDichVu;
    }
}
