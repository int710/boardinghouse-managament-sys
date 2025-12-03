package com.ptpmud.quanlynhatro.controller;

import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.service.TaiKhoanService;
import javax.swing.JOptionPane;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class AuthController {
    private TaiKhoanService service = new TaiKhoanService();

    public TaiKhoan handleLogin(String username, String password) {
        return service.login(username, password);
    }

    public boolean handleRegister(String username, String password, String hoTen, String role) {
        return service.register(username, password, hoTen, role);
    }
}
