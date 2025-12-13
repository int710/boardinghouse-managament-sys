package com.ptpmud.quanlynhatro.dao;

import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public KhachHang findById(int id) {
        String sql = "SELECT * FROM KhachHang WHERE idKhachHang = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
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

    public List<KhachHang> findAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY idKhachHang";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean update(KhachHang k) {
        String sql = """
        UPDATE KhachHang SET 
            tenKhachHang=?, soCccd=?, soDienThoai=?, queQuan=?, ngheNghiep=?, 
            ngaySinh=?, gioiTinh=?, ghiChu=?
        WHERE idKhachHang=?
    """;

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k.getTenKhachHang());
            ps.setString(2, k.getSoCccd());
            ps.setString(3, k.getSoDienThoai());
            ps.setString(4, k.getQueQuan());
            ps.setString(5, k.getNgheNghiep());
            ps.setDate(6, k.getNgaySinh());
            ps.setString(7, k.getGioiTinh());
            ps.setString(8, k.getGhiChu());
            ps.setInt(9, k.getIdKhachHang());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteKhachById(int id) {
        String sql = "DELETE FROM KhachHang WHERE idKhachHang = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int r = ps.executeUpdate();
            return r > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<KhachHang> search(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE tenKhachHang LIKE ? OR soCccd LIKE ?";

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);

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

    public boolean existsCCCD(String cccd) {
        String sql = "SELECT idKhachHang FROM KhachHang WHERE soCccd=? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(tenKhachHang, soCccd, soDienThoai, queQuan, ngheNghiep, ngaySinh, gioiTinh, ghiChu) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSoCccd());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getQueQuan());
            ps.setString(5, kh.getNgheNghiep());
            ps.setDate(6, kh.getNgaySinh());
            ps.setString(7, kh.getGioiTinh());
            ps.setString(8, kh.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public String fetchPhongForKhach(int idKhach) {
        String sql = "SELECT p.tenPhong FROM HopDongThue h JOIN Phong p ON h.idPhong = p.idPhong "
                    + "WHERE h.idKhachHang = ? AND (h.trangThai = 'dangThue' OR h.trangThai = 'dangThue') LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKhach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            // nếu bảng HopDongThue chưa có, ignore quietly
        }
        return "";
    }
    
    public List<String> fetchContractsByCustomer(int idKhach) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT h.idHopDong, p.tenPhong, h.ngayBatDau, h.ngayKetThuc, h.tienCoc, h.trangThai "
                + "FROM HopDongThue h JOIN Phong p ON h.idPhong = p.idPhong WHERE h.idKhachHang = ? ORDER BY h.ngayBatDau DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKhach);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = String.format("HD#%d • Phòng: %s • %s -> %s • Cọc: %,.0f • Trạng thái: %s",
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getDate(3) != null ? rs.getDate(3).toString() : "",
                            rs.getDate(4) != null ? rs.getDate(4).toString() : "",
                            rs.getDouble(5),
                            rs.getString(6));
                    list.add(s);
                }
            }
        } catch (SQLException ex) {
            // maybe table doesn't exist yet — ignore
        }
        return list;
    }
    
     public boolean hasActiveContract(int idKhach) {
        String sql = "SELECT 1 FROM HopDongThue WHERE idKhachHang = ? AND trangThai = 'dangThue' LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKhach);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    // Create HopDongThue + update Phong.trangThai = 'dangThue'
    public boolean createHopDongAndAssignRoom(int idPhong, int idKhachHang, LocalDate ngayBatDau, LocalDate ngayKetThuc, double tienCoc) {
        String insert = "INSERT INTO HopDongThue (idPhong, idKhachHang, ngayBatDau, ngayKetThuc, tienCoc, trangThai) VALUES (?,?,?,?,?,?)";
        String updatePhong = "UPDATE Phong SET trangThai = 'dangThue' WHERE idPhong = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setInt(1, idPhong);
                ps.setInt(2, idKhachHang);
                ps.setDate(3, Date.valueOf(ngayBatDau));
                if (ngayKetThuc != null) {
                    ps.setDate(4, Date.valueOf(ngayKetThuc));
                } else {
                    ps.setNull(4, Types.DATE);
                }
                ps.setDouble(5, tienCoc);
                ps.setString(6, "dangThue");
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(updatePhong)) {
                ps2.setInt(1, idPhong);
                ps2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ignore) {
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (SQLException ignore) {
            }
        }
    }
    
    
}

