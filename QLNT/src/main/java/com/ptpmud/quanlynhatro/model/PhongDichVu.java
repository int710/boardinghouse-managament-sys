package com.ptpmud.quanlynhatro.model;

public class PhongDichVu {
    private int idPhongDichVu;
    private int idPhong;
    private int idDichVu;
    private int soLuong;
    private Integer thang; // nullable
    private Integer nam;   // nullable
    private String ghiChu;
    private String ngayTao;

    // JOIN thêm (tiện cho UI)
    private DichVu dichVu;

    public PhongDichVu() {
    }

    public PhongDichVu(int idPhongDichVu, int idPhong, int idDichVu, int soLuong,
                       Integer thang, Integer nam, String ghiChu, String ngayTao) {
        this.idPhongDichVu = idPhongDichVu;
        this.idPhong = idPhong;
        this.idDichVu = idDichVu;
        this.soLuong = soLuong;
        this.thang = thang;
        this.nam = nam;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
    }

    public int getIdPhongDichVu() {
        return idPhongDichVu;
    }

    public void setIdPhongDichVu(int idPhongDichVu) {
        this.idPhongDichVu = idPhongDichVu;
    }

    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
    }

    public int getIdDichVu() {
        return idDichVu;
    }

    public void setIdDichVu(int idDichVu) {
        this.idDichVu = idDichVu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Integer getThang() {
        return thang;
    }

    public void setThang(Integer thang) {
        this.thang = thang;
    }

    public Integer getNam() {
        return nam;
    }

    public void setNam(Integer nam) {
        this.nam = nam;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }
}
