package com.ptpmud.quanlynhatro.service;

import com.ptpmud.quanlynhatro.dao.HopDongDAO;
import com.ptpmud.quanlynhatro.dao.KhachHangDAO;
import com.ptpmud.quanlynhatro.dao.PhongDAO;
import com.ptpmud.quanlynhatro.model.HopDong;
import com.ptpmud.quanlynhatro.model.KhachHang;
import com.ptpmud.quanlynhatro.model.Phong;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class PhongService {
    private final PhongDAO phongDAO = new PhongDAO();
    private final KhachHangDAO khDAO = new KhachHangDAO();
    private final HopDongDAO hdDAO = new HopDongDAO();
    private final TaiKhoanService tkService = new TaiKhoanService();

    public List<Phong> getAll() { return phongDAO.findAll(); }
    public List<Phong> getByStatus(String s) { return phongDAO.findByStatus(s); }
    public List<Phong> search(String keyword) { return phongDAO.searchByName(keyword); }
    public boolean create(Phong p) { if (p.getTrangThai()==null) p.setTrangThai("trong"); return phongDAO.insert(p); }
    public boolean update(Phong p) { return phongDAO.update(p); }
    public boolean delete(int id) { return phongDAO.delete(id); }
    public boolean setTrangThai(int id, String tt) { return phongDAO.setTrangThai(id, tt); }
    public int countStatus(String s) { return phongDAO.countByStatus(s); }
    public Phong findById(int id) { return phongDAO.findById(id); }
    
    /**
     * Lấy danh sách khách hàng chưa có hợp đồng đang thuê
     */
    public List<KhachHang> getAvailableCustomers() {
        return khDAO.findAvailableCustomers();
    }

    // --- assignTenantToPhong ---
    public AssignResult assignTenantToPhong(int idPhong, int idKhachHang, LocalDate ngayBatDau, LocalDate ngayKetThuc, double tienCoc, boolean createAccountForTenant) {
        // 1) validate room status
        Phong p = phongDAO.findById(idPhong);
        if (p == null) return new AssignResult(false, "Phòng không tồn tại");
        if ("baoTri".equalsIgnoreCase(p.getTrangThai())) return new AssignResult(false, "Phòng đang bảo trì, không thể cho thuê");
        if ("dangThue".equalsIgnoreCase(p.getTrangThai())) return new AssignResult(false, "Phòng đã đang thuê");

        // 2) insert HopDongThue
        HopDong hd = new HopDong();
        hd.setIdPhong(idPhong);
        hd.setIdKhachHang(idKhachHang);
        hd.setNgayBatDau(Date.valueOf(ngayBatDau));
        hd.setNgayKetThuc(ngayKetThuc != null ? Date.valueOf(ngayKetThuc) : null);
        hd.setTienCoc(tienCoc);
        hd.setTrangThai("dangThue");
        boolean inserted = hdDAO.insert(hd);
        if (!inserted) return new AssignResult(false, "Không thể tạo hợp đồng (DB error)");

        // 3) update Phong.trangThai
        boolean updated = phongDAO.setTrangThai(idPhong, "dangThue");
        if (!updated) {
            return new AssignResult(false, "Tạo hợp đồng nhưng không thể cập nhật trạng thái phòng");
        }

        // 4) optional: tạo tài khoản cho khách thuê (user)
        String createdUsername = null;
        String createdPlainPassword = null;
        if (createAccountForTenant) {
            KhachHang kh = khDAO.findById(idKhachHang);
            if (kh != null) {
                // username: sử dụng email nếu có, nếu không thì dùng kh_{id}
                if (kh.getEmail() != null && !kh.getEmail().trim().isEmpty()) {
                    createdUsername = kh.getEmail().trim();
                } else {
                    createdUsername = "kh" + kh.getIdKhachHang();
                }
                createdPlainPassword = generatePassword(8);
                boolean ok = tkService.register(createdUsername, createdPlainPassword, kh.getTenKhachHang(), "user");
                if (!ok) {
                    // không fatal: trả về cảnh báo
                    return new AssignResult(true, "Đã tạo hợp đồng và cập nhật phòng, nhưng không thể tạo tài khoản (username đã tồn tại?)",
                            createdUsername, createdPlainPassword, true);
                }
            }
        }

        return new AssignResult(true, "Gán khách thuê thành công", createdUsername, createdPlainPassword, false);
    }

    private String generatePassword(int len) {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i=0;i<len;i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    public static class AssignResult {
        public final boolean success;
        public final String message;
        public final String username; // nullable
        public final String plainPassword; // nullable
        public final boolean warning; // if true => partial success with warning

        public AssignResult(boolean s, String msg) { this(s,msg,null,null,false); }
        public AssignResult(boolean s, String msg, String u, String p, boolean warn) {
            this.success = s; this.message = msg; this.username = u; this.plainPassword = p; this.warning = warn;
        }
    }

    // return KhachHang + HopDong active for a given phong
    public TenantInfo getTenantInfoByPhong(int idPhong) {
        HopDong hd = hdDAO.findActiveByPhong(idPhong);
        if (hd == null) return null;
        KhachHang kh = khDAO.findById(hd.getIdKhachHang());
        return new TenantInfo(kh, hd);
    }

    public static class TenantInfo {
        public final KhachHang khachHang;
        public final HopDong hopDong;
        public TenantInfo(KhachHang k, HopDong h) { this.khachHang = k; this.hopDong = h; }
    }
}
