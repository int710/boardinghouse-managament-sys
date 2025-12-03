package com.ptpmud.quanlynhatro.model;

import java.sql.Date;

public class HopDongThue {
    private int idHopDong;
    private int idPhong;
    private int idKhachHang;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private double tienCoc;
    private String trangThai;
    private String ghiChu;

    public HopDongThue() {}

    // getters / setters
    public int getIdHopDong() { return idHopDong; }
    public void setIdHopDong(int idHopDong) { this.idHopDong = idHopDong; }
    public int getIdPhong() { return idPhong; }
    public void setIdPhong(int idPhong) { this.idPhong = idPhong; }
    public int getIdKhachHang() { return idKhachHang; }
    public void setIdKhachHang(int idKhachHang) { this.idKhachHang = idKhachHang; }
    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public double getTienCoc() { return tienCoc; }
    public void setTienCoc(double tienCoc) { this.tienCoc = tienCoc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
