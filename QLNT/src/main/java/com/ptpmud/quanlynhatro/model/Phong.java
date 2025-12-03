package com.ptpmud.quanlynhatro.model;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class Phong {
    private int idPhong;
    private String tenPhong;
    private String loaiPhong;
    private double dienTich;
    private double giaThue;
    private String trangThai; // e.g. "trong", "dangThue", "baoTri"
    private String moTa;

    public Phong() {
    }

    public Phong(int idPhong, String tenPhong, String loaiPhong, double dienTich, double giaThue, String trangThai, String moTa) {
        this.idPhong = idPhong;
        this.tenPhong = tenPhong;
        this.loaiPhong = loaiPhong;
        this.dienTich = dienTich;
        this.giaThue = giaThue;
        this.trangThai = trangThai;
        this.moTa = moTa;
    }

    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public double getDienTich() {
        return dienTich;
    }

    public void setDienTich(double dienTich) {
        this.dienTich = dienTich;
    }

    public double getGiaThue() {
        return giaThue;
    }

    public void setGiaThue(double giaThue) {
        this.giaThue = giaThue;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
}
