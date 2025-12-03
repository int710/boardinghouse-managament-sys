package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public KhachHang findById(int id) {
        String sql = "SELECT * FROM KhachHang WHERE idKhachHang = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public List<KhachHang> findAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY idKhachHang";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (tenKhachHang, soCccd, soDienThoai, queQuan, ngheNghiep, ngaySinh, gioiTinh, ghiChu) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSoCccd());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getQueQuan());
            ps.setString(5, kh.getNgheNghiep());
            ps.setDate(6, kh.getNgaySinh());
            ps.setString(7, kh.getGioiTinh());
            ps.setString(8, kh.getGhiChu());
            int r = ps.executeUpdate();
            if (r>0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) kh.setIdKhachHang(gk.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    private KhachHang map(ResultSet rs) throws SQLException {
        KhachHang k = new KhachHang();
        k.setIdKhachHang(rs.getInt("idKhachHang"));
        k.setTenKhachHang(rs.getString("tenKhachHang"));
        k.setSoCccd(rs.getString("soCccd"));
        k.setSoDienThoai(rs.getString("soDienThoai"));
        k.setQueQuan(rs.getString("queQuan"));
        k.setNgheNghiep(rs.getString("ngheNghiep"));
        k.setNgaySinh(rs.getDate("ngaySinh"));
        k.setGioiTinh(rs.getString("gioiTinh"));
        k.setGhiChu(rs.getString("ghiChu"));
        return k;
    }
}
