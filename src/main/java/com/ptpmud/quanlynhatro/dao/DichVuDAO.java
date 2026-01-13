package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.DichVu;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {
    public List<DichVu> findAll() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu ORDER BY idDichVu";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public DichVu findById(int id) {
        String sql = "SELECT * FROM DichVu WHERE idDichVu = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public boolean insert(DichVu dv) {
        String sql = "INSERT INTO DichVu (tenDichVu, donGia, moTa) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setDouble(2, dv.getDonGia());
            ps.setString(3, dv.getMoTa());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) dv.setIdDichVu(gk.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean update(DichVu dv) {
        String sql = "UPDATE DichVu SET tenDichVu = ?, donGia = ?, moTa = ? WHERE idDichVu = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setDouble(2, dv.getDonGia());
            ps.setString(3, dv.getMoTa());
            ps.setInt(4, dv.getIdDichVu());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM DichVu WHERE idDichVu = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    private DichVu map(ResultSet rs) throws SQLException {
        DichVu d = new DichVu();
        d.setIdDichVu(rs.getInt("idDichVu"));
        d.setTenDichVu(rs.getString("tenDichVu"));
        d.setDonGia(rs.getDouble("donGia"));
        d.setMoTa(rs.getString("moTa"));
        return d;
    }
}
