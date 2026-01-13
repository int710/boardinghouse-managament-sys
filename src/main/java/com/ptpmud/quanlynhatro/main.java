package com.ptpmud.quanlynhatro;

import com.ptpmud.quanlynhatro.controller.AppController;
import com.ptpmud.quanlynhatro.utils.DBConnection;
import com.ptpmud.quanlynhatro.view.LoginFrame;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class main {

    public static void main(String[] args) {

        // 1. Khởi tạo hệ thống ở background thread
        new Thread(() -> {
            AppController app = new AppController();
            app.initSystem();   // DB chạy ở đây, không block UI
        }).start();

        // 2. UI chạy riêng trên EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }

}
