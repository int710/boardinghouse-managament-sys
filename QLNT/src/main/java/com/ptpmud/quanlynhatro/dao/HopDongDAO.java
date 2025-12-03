package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.HopDongThue;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HopDongDAO {

    public boolean insert(HopDongThue hd) {
        String sql = "INSERT INTO HopDongThue (idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai, ghiChu) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, hd.getIdPhong());
            ps.setInt(2, hd.getIdKhachHang());
            ps.setDate(3, hd.getNgayBatDau());
            ps.setDate(4, hd.getNgayKetThuc());
            ps.setDouble(5, hd.getTienCoc());
            ps.setString(6, hd.getTrangThai() != null ? hd.getTrangThai() : "dangThue");
            ps.setString(7, hd.getGhiChu());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) hd.setIdHopDong(gk.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public HopDongThue findActiveByPhong(int idPhong) {
        String sql = "SELECT * FROM HopDongThue WHERE idPhong = ? AND (trangThai = 'dangThue') ORDER BY idHopDong DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    private HopDongThue map(ResultSet rs) throws SQLException {
        HopDongThue h = new HopDongThue();
        h.setIdHopDong(rs.getInt("idHopDong"));
        h.setIdPhong(rs.getInt("idPhong"));
        h.setIdKhachHang(rs.getInt("idKhachHang"));
        h.setNgayBatDau(rs.getDate("ngayBatDau"));
        h.setNgayKetThuc(rs.getDate("ngayKetThuc"));
        h.setTienCoc(rs.getDouble("tienCoc"));
        h.setTrangThai(rs.getString("trangThai"));
        h.setGhiChu(rs.getString("ghiChu"));
        return h;
    }
}
