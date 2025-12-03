package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.utils.DBConnection;
import java.sql.*;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class TaiKhoanDAO {

    public TaiKhoan findByUsername(String username) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setIdTaiKhoan(rs.getInt("idTaiKhoan"));
                tk.setTenDangNhap(rs.getString("tenDangNhap"));
                tk.setMatKhau(rs.getString("matKhau"));
                tk.setVaiTro(rs.getString("vaiTro"));
                tk.setHoTen(rs.getString("hoTen"));
                return tk;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen) VALUES (?,?,?,?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getVaiTro());
            ps.setString(4, tk.getHoTen());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAdminExists() {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE vaiTro = 'admin'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
