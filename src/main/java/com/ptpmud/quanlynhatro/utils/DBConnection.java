package com.ptpmud.quanlynhatro.utils;

/**
 *
 * @author Bùi Thanh Quân - int710 - CT070242
 */
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnection {
    // Deploy db test
    private static final String JDBC_URL = "jdbc:mysql://shuttle.proxy.rlwy.net:27762/QuanLyNhaTro?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "mJhlOyTjMmcrOmXlwBHrQDmkDeUaJLXE";

    // Kết nối tới MySQL cài trên máy cá nhân (Localhost)
//    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/QuanLyNhaTro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
//    private static final String USER = "root";
//    private static final String PASS = "07102004";

    private static final HikariDataSource DATA_SOURCE;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName("qlnt-hikari");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "UTF-8");
        config.addDataSourceProperty("tcpKeepAlive", "true");
        config.addDataSourceProperty("connectTimeout", "5000");
        config.addDataSourceProperty("socketTimeout", "15000");
        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        DATA_SOURCE = new HikariDataSource(config);
    }

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Kết nối DB thất bại", e);
        }
    }
}
