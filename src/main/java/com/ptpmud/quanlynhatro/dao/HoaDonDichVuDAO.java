package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.HoaDonDichVu;
import com.ptpmud.quanlynhatro.model.DichVu;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDichVuDAO {

    public List<HoaDonDichVu> findByHoaDon(int idHoaDon) {
        List<HoaDonDichVu> list = new ArrayList<>();
        String sql = """
            SELECT hddv.*, dv.tenDichVu, dv.moTa
            FROM HoaDonDichVu hddv
            JOIN DichVu dv ON hddv.idDichVu = dv.idDichVu
            WHERE hddv.idHoaDon = ?
            ORDER BY hddv.idHoaDonDichVu
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDonDichVu hddv = map(rs);
                    DichVu dv = new DichVu();
                    dv.setIdDichVu(rs.getInt("idDichVu"));
                    dv.setTenDichVu(rs.getString("tenDichVu"));
                    dv.setMoTa(rs.getString("moTa"));
                    hddv.setDichVu(dv);
                    list.add(hddv);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean insert(HoaDonDichVu hddv) {
        String sql = "INSERT INTO HoaDonDichVu (idHoaDon, idDichVu, soLuong, donGia) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, hddv.getIdHoaDon());
            ps.setInt(2, hddv.getIdDichVu());
            ps.setInt(3, hddv.getSoLuong());
            ps.setDouble(4, hddv.getDonGia());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        hddv.setIdHoaDonDichVu(gk.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteByHoaDon(int idHoaDon) {
        String sql = "DELETE FROM HoaDonDichVu WHERE idHoaDon = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idHoaDon);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int idHoaDonDichVu) {
        String sql = "DELETE FROM HoaDonDichVu WHERE idHoaDonDichVu = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idHoaDonDichVu);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private HoaDonDichVu map(ResultSet rs) throws SQLException {
        HoaDonDichVu hddv = new HoaDonDichVu();
        hddv.setIdHoaDonDichVu(rs.getInt("idHoaDonDichVu"));
        hddv.setIdHoaDon(rs.getInt("idHoaDon"));
        hddv.setIdDichVu(rs.getInt("idDichVu"));
        hddv.setSoLuong(rs.getInt("soLuong"));
        hddv.setDonGia(rs.getDouble("donGia"));
        hddv.setThanhTien(rs.getDouble("thanhTien"));
        return hddv;
    }
}

