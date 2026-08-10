package com.vxl.loi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class VXLCoSoDuLieu {
    private static final Object LOCK = new Object();
    private static HikariDataSource dataSource;

    @FunctionalInterface
    public interface SqlWork {
        void run(Connection conn) throws SQLException;
    }

    private VXLCoSoDuLieu() {
    }

    public static void khoiTao(String mayChu, String database, String user, String pass) {
        if (mayChu == null || database == null || user == null || pass == null) {
            throw new IllegalArgumentException("Database configuration must not be null");
        }
        synchronized (LOCK) {
            closeLocked();
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
            System.out.println("Database connection pool ready: " + config.getJdbcUrl());
        }
    }

    public static Connection getConnection() throws SQLException {
        synchronized (LOCK) {
            if (dataSource == null || dataSource.isClosed()) {
                throw new SQLException("Database connection pool is not initialized");
            }
            return dataSource.getConnection();
        }
    }

    public static void withConnection(SqlWork work) throws SQLException {
        if (work == null) {
            throw new IllegalArgumentException("SQL work must not be null");
        }
        try (Connection conn = getConnection()) {
            work.run(conn);
        }
    }

    public static void withTransaction(SqlWork work) throws SQLException {
        if (work == null) {
            throw new IllegalArgumentException("SQL work must not be null");
        }
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                work.run(conn);
                conn.commit();
            }
            catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                }
                catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
                throw ex;
            }
        }
    }

    public static void close() {
        synchronized (LOCK) {
            closeLocked();
        }
    }

    private static void closeLocked() {
        if (dataSource != null) {
            if (!dataSource.isClosed()) {
                dataSource.close();
            }
            dataSource = null;
        }
    }
}
