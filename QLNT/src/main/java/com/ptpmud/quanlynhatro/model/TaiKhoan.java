package com.ptpmud.quanlynhatro.model;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class TaiKhoan {
    private int idTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private String hoTen;

    public TaiKhoan() {}

    public TaiKhoan(int id, String user, String pass, String role, String name) {
        this.idTaiKhoan = id;
        this.tenDangNhap = user;
        this.matKhau = pass;
        this.vaiTro = role;
        this.hoTen = name;
    }

    public int getIdTaiKhoan() {
        return idTaiKhoan;
    }

    public void setIdTaiKhoan(int idTaiKhoan) {
        this.idTaiKhoan = idTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    
}
