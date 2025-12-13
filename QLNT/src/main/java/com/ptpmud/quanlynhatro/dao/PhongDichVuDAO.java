package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.PhongDichVu;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDichVuDAO {

    public List<PhongDichVu> findByPhong(int idPhong) {
        List<PhongDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM PhongDichVu WHERE idPhong = ? ORDER BY ngayTao DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public boolean insert(PhongDichVu pdv) {
        String sql = "INSERT INTO PhongDichVu (idPhong, idDichVu, soLuong, thang, nam, ghiChu) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pdv.getIdPhong());
            ps.setInt(2, pdv.getIdDichVu());
            ps.setInt(3, pdv.getSoLuong());
            if (pdv.getThang() == null) ps.setNull(4, Types.TINYINT); else ps.setInt(4, pdv.getThang());
            if (pdv.getNam() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, pdv.getNam());
            ps.setString(6, pdv.getGhiChu());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) pdv.setIdPhongDichVu(gk.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean remove(int idPhongDichVu) {
        String sql = "DELETE FROM PhongDichVu WHERE idPhongDichVu = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhongDichVu);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    private PhongDichVu map(ResultSet rs) throws SQLException {
        PhongDichVu p = new PhongDichVu();
        p.setIdPhongDichVu(rs.getInt("idPhongDichVu"));
        p.setIdPhong(rs.getInt("idPhong"));
        p.setIdDichVu(rs.getInt("idDichVu"));
        p.setSoLuong(rs.getInt("soLuong"));
        int th = rs.getInt("thang"); if (rs.wasNull()) p.setThang(null); else p.setThang(th);
        int nm = rs.getInt("nam"); if (rs.wasNull()) p.setNam(null); else p.setNam(nm);
        p.setGhiChu(rs.getString("ghiChu"));
        return p;
    }
}
