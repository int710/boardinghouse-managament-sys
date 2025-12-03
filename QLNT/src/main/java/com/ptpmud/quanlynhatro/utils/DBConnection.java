package com.ptpmud.quanlynhatro.utils;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/QuanLyNhaTro?useSSL=false&serverTimezone=UTC&adminadmallowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "07102004";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Kết nối DB thất bại: " + e.getMessage());
            return null;
        }
    }
}
