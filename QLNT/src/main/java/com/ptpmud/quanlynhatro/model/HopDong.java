// 1. Model: HopDong.java
// Package: com.quanlynhatro.model
package com.ptpmud.quanlynhatro.model;

import java.time.LocalDate;
import java.util.Date;

public class HopDong {
    private int idHopDong;
    private int idPhong;
    private int idKhachHang;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private double tienCoc;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;

    // Constructors
    public HopDong() {}

    public HopDong(int idPhong, int idKhachHang, Date ngayBatDau, Date ngayKetThuc, double tienCoc, String trangThai, String ghiChu) {
        this.idPhong = idPhong;
        this.idKhachHang = idKhachHang;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    // Getters and Setters
    public int getIdHopDong() {
        return idHopDong;
    }

    public void setIdHopDong(int idHopDong) {
        this.idHopDong = idHopDong;
    }

    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
    }

    public int getIdKhachHang() {
        return idKhachHang;
    }

    public void setIdKhachHang(int idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau != null ? java.sql.Date.valueOf(ngayBatDau) : null;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc != null ? java.sql.Date.valueOf(ngayKetThuc) : null;
    }

    public double getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(double tienCoc) {
        this.tienCoc = tienCoc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDate getNgayBatDauAsLocal() {
        return ngayBatDau == null ? null : new java.sql.Date(ngayBatDau.getTime()).toLocalDate();
    }

    public LocalDate getNgayKetThucAsLocal() {
        return ngayKetThuc == null ? null : new java.sql.Date(ngayKetThuc.getTime()).toLocalDate();
    }

    @Override
    public String toString() {
        return "HopDong{" +
                "idHopDong=" + idHopDong +
                ", idPhong=" + idPhong +
                ", idKhachHang=" + idKhachHang +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", tienCoc=" + tienCoc +
                ", trangThai='" + trangThai + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", ngayTao=" + ngayTao +
                '}';
    }
}