package com.ptpmud.quanlynhatro.controller;

import com.ptpmud.quanlynhatro.dao.TaiKhoanDAO;
import com.ptpmud.quanlynhatro.model.TaiKhoan;
import com.ptpmud.quanlynhatro.service.TaiKhoanService;
import com.ptpmud.quanlynhatro.view.AdminFrame;


/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
public class AppController {
    private final TaiKhoanDAO taiKhoanDAO;
    private final TaiKhoanService taiKhoanService;

    public AppController() {
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.taiKhoanService = new TaiKhoanService();
    }

    // Gọi khi mở app
    public void initSystem() {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        try {
            boolean exists = taiKhoanDAO.isAdminExists();
            if (!exists) {
                boolean created = taiKhoanService.register("admin", "admin", "Quản trị hệ thống", "admin");
                if (created) {
                    System.out.println("Đã tạo tài khoản admin mặc định.");
                } else {
                    System.out.println("Không thể tạo tài khoản admin mặc định.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void openUIByRole(TaiKhoan tk) {
        String role = tk.getVaiTro();
        if (role.equalsIgnoreCase("admin")) {
            AdminFrame adminUI = new AdminFrame(tk);
            adminUI.setVisible(true);
        } else {
            // Người thuê: mở màn hình xem hóa đơn
            new com.ptpmud.quanlynhatro.view.UserHoaDonFrame(tk).setVisible(true);
        }
    }

}
