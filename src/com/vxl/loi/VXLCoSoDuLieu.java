package com.vxl.loi;

// Vũ Xuân Lâm đẹp trai VCL
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class VXLCoSoDuLieu {
    private static HikariDataSource dataSource;

    @FunctionalInterface
    public interface SqlWork {
        void run(Connection conn) throws SQLException;
    }

    private VXLCoSoDuLieu() {
    }

    public static void khoiTao(String mayChu, String database, String user, String pass) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + mayChu + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(30);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10000L);
        config.setPoolName("VXLCheo3Pool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource = new HikariDataSource(config);
        System.out.println("VXLCoSoDuLieu pool ready: " + config.getJdbcUrl());
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void withConnection(SqlWork work) throws SQLException {
        try (Connection conn = getConnection()) {
            work.run(conn);
        }
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

