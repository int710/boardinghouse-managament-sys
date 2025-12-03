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
        SwingUtilities.invokeLater(() -> {
            AppController app = new AppController();
            app.initSystem();  // tạo admin nếu chưa có
            
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
