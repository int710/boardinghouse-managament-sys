package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.HoaDon;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public boolean upsertByProcedure(int idPhong, int thang, int nam) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement cs = c.prepareCall("{CALL spTaoHoaDonChoPhong(?,?,?)}")) {
            cs.setInt(1, idPhong);
            cs.setInt(2, thang);
            cs.setInt(3, nam);
            cs.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<HoaDon> findAll(Integer thang, Integer nam, String trangThai) {
        StringBuilder sb = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1");
        if (thang != null) sb.append(" AND thang = ?");
        if (nam != null) sb.append(" AND nam = ?");
        if (trangThai != null && !trangThai.isEmpty()) sb.append(" AND trangThai = ?");
        sb.append(" ORDER BY nam DESC, thang DESC, ngayTao DESC");
        List<HoaDon> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            int idx = 1;
            if (thang != null) ps.setInt(idx++, thang);
            if (nam != null) ps.setInt(idx++, nam);
            if (trangThai != null && !trangThai.isEmpty()) ps.setString(idx, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public List<HoaDon> findByPhong(int idPhong) {
        String sql = "SELECT * FROM HoaDon WHERE idPhong = ? ORDER BY nam DESC, thang DESC";
        List<HoaDon> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public HoaDon findOne(int idPhong, int thang, int nam) {
        String sql = "SELECT * FROM HoaDon WHERE idPhong=? AND thang=? AND nam=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public HoaDon findById(int id) {
        String sql = "SELECT * FROM HoaDon WHERE idHoaDon = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public boolean exists(int idPhong, int thang, int nam) {
        String sql = "SELECT 1 FROM HoaDon WHERE idPhong=? AND thang=? AND nam=? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE HoaDon SET trangThai = ? WHERE idHoaDon = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean updateTienKhac(int idHoaDon, double tienKhac) {
        String sql = "UPDATE HoaDon SET tienKhac = ?, tongTien = tienPhong + tienDien + tienNuoc + tienDichVu + ? WHERE idHoaDon = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, tienKhac);
            ps.setDouble(2, tienKhac);
            ps.setInt(3, idHoaDon);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean updateManual(HoaDon h) {
        String sql = """
            UPDATE HoaDon SET
                tienPhong=?, tienDien=?, tienNuoc=?, tienDichVu=?, tienKhac=?, tongTien=?, trangThai=?
            WHERE idHoaDon=?
        """;
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, h.getTienPhong());
            ps.setDouble(2, h.getTienDien());
            ps.setDouble(3, h.getTienNuoc());
            ps.setDouble(4, h.getTienDichVu());
            ps.setDouble(5, h.getTienKhac());
            ps.setDouble(6, h.getTongTien());
            ps.setString(7, h.getTrangThai());
            ps.setInt(8, h.getIdHoaDon());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM HoaDon WHERE idHoaDon = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    /**
     * Lấy chỉ số điện cũ từ hóa đơn tháng trước (từ bảng Dien của tháng trước).
     * Nếu không có thì trả về 0.
     */
    public double getLastDienChiSoMoi(int idPhong, int thang, int nam) {
        // Tìm tháng trước
        int prevThang = thang - 1;
        int prevNam = nam;
        if (prevThang < 1) {
            prevThang = 12;
            prevNam = nam - 1;
        }
        String sql = "SELECT chiSoMoi FROM Dien WHERE idPhong=? AND thang=? AND nam=? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            ps.setInt(2, prevThang);
            ps.setInt(3, prevNam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        // Nếu không có tháng trước, lấy chỉ số mới nhất
        String sql2 = "SELECT chiSoMoi FROM Dien WHERE idPhong=? ORDER BY nam DESC, thang DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql2)) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    /**
     * Lấy số nước cũ từ hóa đơn tháng trước.
     * Tính tổng số khối đã dùng của tất cả các tháng trước tháng hiện tại.
     */
    public double getLastNuocSoKhoi(int idPhong, int thang, int nam) {
        // Tính tổng số khối đã dùng của tất cả các tháng trước tháng hiện tại
        String sql = """
            SELECT COALESCE(SUM(soKhoi), 0) 
            FROM Nuoc 
            WHERE idPhong = ? 
            AND (nam < ? OR (nam = ? AND thang < ?))
        """;
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            ps.setInt(2, nam);
            ps.setInt(3, nam);
            ps.setInt(4, thang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    public boolean upsertDien(int idPhong, int thang, int nam, double cu, double moi, double donGia) {
        String sql = """
            INSERT INTO Dien (idPhong, thang, nam, chiSoCu, chiSoMoi, donGia)
            VALUES (?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE chiSoCu=VALUES(chiSoCu), chiSoMoi=VALUES(chiSoMoi), donGia=VALUES(donGia)
        """;
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ps.setDouble(4, cu);
            ps.setDouble(5, moi);
            ps.setDouble(6, donGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean upsertNuoc(int idPhong, int thang, int nam, double cu, double moi, double donGia) {
        String sql = """
            INSERT INTO Nuoc (idPhong, thang, nam, soKhoi, donGia)
            VALUES (?,?,?,?,?)
            ON DUPLICATE KEY UPDATE soKhoi=VALUES(soKhoi), donGia=VALUES(donGia)
        """;
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPhong);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ps.setDouble(4, moi - cu); // lưu số khối dùng
            ps.setDouble(5, donGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public double sumTongTienByMonth(int thang, int nam) {
        String sql = "SELECT COALESCE(SUM(tongTien),0) FROM HoaDon WHERE thang=? AND nam=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    private HoaDon map(ResultSet rs) throws SQLException {
        HoaDon h = new HoaDon();
        h.setIdHoaDon(rs.getInt("idHoaDon"));
        h.setIdPhong(rs.getInt("idPhong"));
        h.setThang(rs.getInt("thang"));
        h.setNam(rs.getInt("nam"));
        h.setTienPhong(rs.getDouble("tienPhong"));
        h.setTienDien(rs.getDouble("tienDien"));
        h.setTienNuoc(rs.getDouble("tienNuoc"));
        h.setTienDichVu(rs.getDouble("tienDichVu"));
        h.setTienKhac(rs.getDouble("tienKhac"));
        h.setTongTien(rs.getDouble("tongTien"));
        h.setTrangThai(rs.getString("trangThai"));
        h.setNgayTao(rs.getTimestamp("ngayTao"));
        return h;
    }
}

