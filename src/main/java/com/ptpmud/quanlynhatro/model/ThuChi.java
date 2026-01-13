package com.ptpmud.quanlynhatro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class for ThuChi (Revenue and Expense) table
 * 
 * @author Admin
 */
public class ThuChi {
    private int idThuChi;
    private LocalDate ngayLap;          // Date of transaction
    private String loai;                 // "THU" (Revenue) or "CHI" (Expense)
    private double soTien;               // Amount
    private String danhMuc;              // Category (TienPhong, SuaChua, TienDien, VeSinh, MuaSam, etc.)
    private String ghiChu;               // Note/Description
    private Integer idPhong;             // Room ID (can be null for general expenses)
    private LocalDateTime ngayTao;       // Creation timestamp

    // Constructors
    public ThuChi() {
    }

    public ThuChi(LocalDate ngayLap, String loai, double soTien, String danhMuc, String ghiChu, Integer idPhong) {
        this.ngayLap = ngayLap;
        this.loai = loai;
        this.soTien = soTien;
        this.danhMuc = danhMuc;
        this.ghiChu = ghiChu;
        this.idPhong = idPhong;
    }

    public ThuChi(int idThuChi, LocalDate ngayLap, String loai, double soTien, String danhMuc, String ghiChu, Integer idPhong, LocalDateTime ngayTao) {
        this.idThuChi = idThuChi;
        this.ngayLap = ngayLap;
        this.loai = loai;
        this.soTien = soTien;
        this.danhMuc = danhMuc;
        this.ghiChu = ghiChu;
        this.idPhong = idPhong;
        this.ngayTao = ngayTao;
    }

    // Getters and Setters
    public int getIdThuChi() {
        return idThuChi;
    }

    public void setIdThuChi(int idThuChi) {
        this.idThuChi = idThuChi;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Integer getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(Integer idPhong) {
        this.idPhong = idPhong;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return "ThuChi{" +
                "idThuChi=" + idThuChi +
                ", ngayLap=" + ngayLap +
                ", loai='" + loai + '\'' +
                ", soTien=" + soTien +
                ", danhMuc='" + danhMuc + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", idPhong=" + idPhong +
                ", ngayTao=" + ngayTao +
                '}';
    }
}
