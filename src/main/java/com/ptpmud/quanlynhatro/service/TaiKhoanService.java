package com.ptpmud.quanlynhatro.service;

import com.ptpmud.quanlynhatro.dao.TaiKhoanDAO;
import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.utils.Utils;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class TaiKhoanService {

    private TaiKhoanDAO dao = new TaiKhoanDAO();

    public TaiKhoan login(String username, String password) {
        TaiKhoan tk = dao.findByUsername(username);
        if (tk == null) {
            return null;
        }
        if (Utils.checkPassword(password, tk.getMatKhau())) {
            return tk;
        }
        return null;
    }

    public boolean register(String username, String password, String hoTen, String role) {
        return register(username, password, hoTen, role, null);
    }

    public boolean register(String username, String password, String hoTen, String role, Integer idKhachHang) {
        if (dao.findByUsername(username) != null) {
            return false;
        }
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(username);
        tk.setMatKhau(Utils.hashPassword(password));
        tk.setVaiTro(role);
        tk.setHoTen(hoTen);
        tk.setIdKhachHang(idKhachHang);
        return dao.addUser(tk);
    }

    public java.util.List<TaiKhoan> findAll() {
        return dao.findAll();
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }

    public TaiKhoan findByKhachHang(int idKhachHang) {
        return dao.findByKhachHang(idKhachHang);
    }

    public boolean updateKhachHangLink(int idTaiKhoan, Integer idKhachHang) {
        return dao.updateKhachHangLink(idTaiKhoan, idKhachHang);
    }

    public Integer getKhachHangIdFromTaiKhoan(TaiKhoan tk) {
        if (tk == null) return null;
        if (tk.getIdKhachHang() != null) {
            return tk.getIdKhachHang();
        }
        return parseKhachHangIdFromUsername(tk.getTenDangNhap());
    }

    public int parseKhachHangIdFromUsername(String username) {
        if (username == null) {
            return -1;
        }
        if (username.startsWith("kh")) {
            try {
                return Integer.parseInt(username.substring(2));
            } catch (NumberFormatException ignore) {
            }
        }
        return -1;
    }

    // Hàm này dùng cho user
    public boolean changePassword(
                int idTaiKhoan,
                String oldPasswordInput,
                String newPassword,
                String confirmPassword
    ) {
        TaiKhoan tk = dao.findById(idTaiKhoan);
        if (tk == null) {
            throw new IllegalArgumentException("Tài khoản không tồn tại");
        }
        // 1. Kiểm tra mật khẩu cũ
        if (!Utils.checkPassword(oldPasswordInput, tk.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        // 2. Validate mật khẩu mới
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }
        // 3. Không cho trùng mật khẩu cũ
        if (Utils.checkPassword(newPassword, tk.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng mật khẩu cũ");
        }
        // 4. Hash & update
        String newHashed = Utils.hashPassword(newPassword);
        return dao.updatePassword(idTaiKhoan, newHashed);
    }
    
    // Hàm này dành riêng cho Admin
    public boolean adminChangePassword(int idTaiKhoan, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        String hashed = Utils.hashPassword(newPassword);
        return dao.updatePassword(idTaiKhoan, hashed);
    }

}
