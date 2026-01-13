package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.ThuChi;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThuChiDAO {

    public List<ThuChi> findAll() {
        List<ThuChi> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuChi ORDER BY ngayLap DESC, idThuChi DESC";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public ThuChi findById(int id) {
        String sql = "SELECT * FROM ThuChi WHERE idThuChi = ?";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ThuChi> findByLoai(String loai) {
        List<ThuChi> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuChi WHERE loai = ? ORDER BY ngayLap DESC";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, loai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<ThuChi> findByDanhMuc(String danhMuc) {
        List<ThuChi> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuChi WHERE danhMuc = ? ORDER BY ngayLap DESC";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, danhMuc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<ThuChi> findByMonth(int month, int year) {
        List<ThuChi> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuChi WHERE MONTH(ngayLap) = ? AND YEAR(ngayLap) = ? ORDER BY ngayLap DESC";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<ThuChi> findByMonthAndLoai(int month, int year, String loai) {
        List<ThuChi> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuChi WHERE MONTH(ngayLap) = ? AND YEAR(ngayLap) = ? AND loai = ? ORDER BY ngayLap DESC";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setString(3, loai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<ThuChi> findByPhong(int idPhong) {
        List<ThuChi> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuChi WHERE idPhong = ? ORDER BY ngayLap DESC";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean insert(ThuChi tc) {
        String sql = "INSERT INTO ThuChi (ngayLap, loai, soTien, danhMuc, ghiChu, idPhong) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, java.sql.Date.valueOf(tc.getNgayLap()));
            ps.setString(2, tc.getLoai());
            ps.setDouble(3, tc.getSoTien());
            ps.setString(4, tc.getDanhMuc());
            ps.setString(5, tc.getGhiChu());
            if (tc.getIdPhong() != null) {
                ps.setInt(6, tc.getIdPhong());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        tc.setIdThuChi(gk.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(ThuChi tc) {
        String sql = "UPDATE ThuChi SET ngayLap=?, loai=?, soTien=?, danhMuc=?, ghiChu=?, idPhong=? WHERE idThuChi=?";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(tc.getNgayLap()));
            ps.setString(2, tc.getLoai());
            ps.setDouble(3, tc.getSoTien());
            ps.setString(4, tc.getDanhMuc());
            ps.setString(5, tc.getGhiChu());
            if (tc.getIdPhong() != null) {
                ps.setInt(6, tc.getIdPhong());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, tc.getIdThuChi());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM ThuChi WHERE idThuChi = ?";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public double sumByMonthAndLoai(int month, int year, String loai) {
        String sql = "SELECT COALESCE(SUM(soTien), 0) FROM ThuChi WHERE MONTH(ngayLap) = ? AND YEAR(ngayLap) = ? AND loai = ?";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setString(3, loai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public double sumByDanhMucAndMonth(String danhMuc, int month, int year) {
        String sql = "SELECT COALESCE(SUM(soTien), 0) FROM ThuChi WHERE danhMuc = ? AND MONTH(ngayLap) = ? AND YEAR(ngayLap) = ?";
        try (Connection c = DBConnection.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, danhMuc);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private ThuChi map(ResultSet rs) throws SQLException {
        ThuChi tc = new ThuChi();
        tc.setIdThuChi(rs.getInt("idThuChi"));
        tc.setNgayLap(rs.getDate("ngayLap").toLocalDate());
        tc.setLoai(rs.getString("loai"));
        tc.setSoTien(rs.getDouble("soTien"));
        tc.setDanhMuc(rs.getString("danhMuc"));
        tc.setGhiChu(rs.getString("ghiChu"));
        int idPhong = rs.getInt("idPhong");
        tc.setIdPhong(rs.wasNull() ? null : idPhong);
        tc.setNgayTao(rs.getTimestamp("ngayTao").toLocalDateTime());
        return tc;
    }
}
