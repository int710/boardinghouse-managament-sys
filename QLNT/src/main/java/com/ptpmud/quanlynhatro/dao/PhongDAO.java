package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.Phong;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDAO {

    public List<Phong> findAll() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong ORDER BY idPhong";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public List<Phong> findByStatus(String status) {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong WHERE trangThai = ? ORDER BY idPhong";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public List<Phong> searchByName(String keyword) {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong WHERE tenPhong LIKE ? OR moTa LIKE ? ORDER BY idPhong";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public Phong findById(int id) {
        String sql = "SELECT * FROM Phong WHERE idPhong = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public boolean insert(Phong p) {
        String sql = "INSERT INTO Phong (tenPhong, loaiPhong, dienTich, giaThue, trangThai, moTa) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTenPhong());
            ps.setString(2, p.getLoaiPhong());
            ps.setDouble(3, p.getDienTich());
            ps.setDouble(4, p.getGiaThue());
            ps.setString(5, p.getTrangThai());
            ps.setString(6, p.getMoTa());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) p.setIdPhong(gk.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean update(Phong p) {
        String sql = "UPDATE Phong SET tenPhong=?, loaiPhong=?, dienTich=?, giaThue=?, trangThai=?, moTa=? WHERE idPhong=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTenPhong());
            ps.setString(2, p.getLoaiPhong());
            ps.setDouble(3, p.getDienTich());
            ps.setDouble(4, p.getGiaThue());
            ps.setString(5, p.getTrangThai());
            ps.setString(6, p.getMoTa());
            ps.setInt(7, p.getIdPhong());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Phong WHERE idPhong = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean setTrangThai(int id, String trangThai) {
        String sql = "UPDATE Phong SET trangThai = ? WHERE idPhong = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Phong WHERE trangThai = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    private Phong map(ResultSet rs) throws SQLException {
        Phong p = new Phong();
        p.setIdPhong(rs.getInt("idPhong"));
        p.setTenPhong(rs.getString("tenPhong"));
        p.setLoaiPhong(rs.getString("loaiPhong"));
        p.setDienTich(rs.getDouble("dienTich"));
        p.setGiaThue(rs.getDouble("giaThue"));
        p.setTrangThai(rs.getString("trangThai"));
        p.setMoTa(rs.getString("moTa"));
        return p;
    }
}
