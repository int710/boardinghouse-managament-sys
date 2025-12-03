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
        // Validate tài khoản trước trong db
        if (dao.findByUsername(username) != null) {
            return false;
        }
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(username);
        tk.setMatKhau(Utils.hashPassword(password));
        tk.setVaiTro(role);
        tk.setHoTen(hoTen);
        return dao.addUser(tk);
    }
}
