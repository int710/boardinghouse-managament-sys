package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.HopDong;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HopDongDAO {

    public boolean insert(HopDong hopDong) {
        String sql = """
                INSERT INTO HopDongThue (idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai, ghiChu)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, hopDong.getIdPhong());
            ps.setInt(2, hopDong.getIdKhachHang());
            ps.setDate(3, new Date(hopDong.getNgayBatDau().getTime()));
            if (hopDong.getNgayKetThuc() != null) {
                ps.setDate(4, new Date(hopDong.getNgayKetThuc().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setDouble(5, hopDong.getTienCoc());
            ps.setString(6, hopDong.getTrangThai());
            ps.setString(7, hopDong.getGhiChu());
            int r = ps.executeUpdate();
            if (r > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) hopDong.setIdHopDong(gk.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(HopDong hopDong) {
        String sql = """
                UPDATE HopDongThue
                SET idPhong=?, idKhachHang=?, ngayBatDau=?, ngayKetThuc=?, tienCoc=?, trangThai=?, ghiChu=?
                WHERE idHopDong=?
                """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, hopDong.getIdPhong());
            ps.setInt(2, hopDong.getIdKhachHang());
            ps.setDate(3, new Date(hopDong.getNgayBatDau().getTime()));
            if (hopDong.getNgayKetThuc() != null) {
                ps.setDate(4, new Date(hopDong.getNgayKetThuc().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setDouble(5, hopDong.getTienCoc());
            ps.setString(6, hopDong.getTrangThai());
            ps.setString(7, hopDong.getGhiChu());
            ps.setInt(8, hopDong.getIdHopDong());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean endContract(int idHopDong, LocalDate endDate) {
        String sql = "UPDATE HopDongThue SET ngayKetThuc = ?, trangThai = 'daKetThuc' WHERE idHopDong = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (endDate != null) {
                ps.setDate(1, Date.valueOf(endDate));
            } else {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
            }
            ps.setInt(2, idHopDong);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean renewContract(int idHopDong, LocalDate newEndDate) {
        String sql = "UPDATE HopDongThue SET ngayKetThuc = ?, trangThai = 'dangThue' WHERE idHopDong = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (newEndDate != null) {
                ps.setDate(1, Date.valueOf(newEndDate));
            } else {
                ps.setNull(1, Types.DATE);
            }
            ps.setInt(2, idHopDong);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM HopDongThue WHERE idHopDong = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public HopDong findById(int id) {
        String sql = "SELECT * FROM HopDongThue WHERE idHopDong = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public HopDong findActiveByPhong(int idPhong) {
        String sql = "SELECT * FROM HopDongThue WHERE idPhong = ? AND trangThai = 'dangThue' ORDER BY ngayBatDau DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public List<HopDong> findAll() {
        String sql = "SELECT * FROM HopDongThue ORDER BY ngayBatDau DESC";
        return queryList(sql, null);
    }

    public List<HopDong> findByStatus(String status) {
        String sql = "SELECT * FROM HopDongThue WHERE trangThai = ? ORDER BY ngayBatDau DESC";
        return queryList(sql, ps -> ps.setString(1, status));
    }

    public List<HopDong> findHistoryByPhong(int idPhong) {
        String sql = "SELECT * FROM HopDongThue WHERE idPhong = ? ORDER BY ngayBatDau DESC";
        return queryList(sql, ps -> ps.setInt(1, idPhong));
    }

    public HopDong findActiveByKhachHang(int idKhachHang) {
        String sql = "SELECT * FROM HopDongThue WHERE idKhachHang = ? AND trangThai = 'dangThue' ORDER BY ngayBatDau DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public List<HopDong> search(String keyword) {
        // tìm theo idPhòng, idKhachHang, tên phòng, tên khách
        String sql = """
                SELECT hd.* FROM HopDongThue hd
                LEFT JOIN Phong p ON p.idPhong = hd.idPhong
                LEFT JOIN KhachHang kh ON kh.idKhachHang = hd.idKhachHang
                WHERE p.tenPhong LIKE ? OR kh.tenKhachHang LIKE ? OR hd.idPhong = ? OR hd.idKhachHang = ?
                ORDER BY hd.ngayBatDau DESC
                """;
        return queryList(sql, ps -> {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            int maybeId;
            try { maybeId = Integer.parseInt(keyword); }
            catch (NumberFormatException ex) { maybeId = -1; }
            ps.setInt(3, maybeId);
            ps.setInt(4, maybeId);
        });
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM HopDongThue";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM HopDongThue WHERE trangThai = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    private List<HopDong> queryList(String sql, SqlConsumer setter) {
        List<HopDong> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (setter != null) setter.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private HopDong map(ResultSet rs) throws SQLException {
        HopDong hd = new HopDong();
        hd.setIdHopDong(rs.getInt("idHopDong"));
        hd.setIdPhong(rs.getInt("idPhong"));
        hd.setIdKhachHang(rs.getInt("idKhachHang"));
        hd.setNgayBatDau(rs.getDate("ngayBatDau"));
        hd.setNgayKetThuc(rs.getDate("ngayKetThuc"));
        hd.setTienCoc(rs.getDouble("tienCoc"));
        hd.setTrangThai(rs.getString("trangThai"));
        hd.setGhiChu(rs.getString("ghiChu"));
        hd.setNgayTao(rs.getDate("ngayTao"));
        return hd;
    }

    @FunctionalInterface
    private interface SqlConsumer { void accept(PreparedStatement ps) throws SQLException; }
}