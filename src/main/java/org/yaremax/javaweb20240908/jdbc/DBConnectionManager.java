package org.yaremax.javaweb20240908.jdbc;

import jakarta.persistence.EntityManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
    private static DBConnectionManager instance;
    private static final String URL = "jdbc:postgresql://167.99.244.128:5432/ag_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "pass";

    private DBConnectionManager() {}

    public static synchronized DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    public Connection openConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        /*
        TRANSACTION_READ_UNCOMMITTED
        TRANSACTION_READ_COMMITTED
        TRANSACTION_REPEATABLE_READ
        TRANSACTION_SERIALIZABLE

        To change isolation
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
         */
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
