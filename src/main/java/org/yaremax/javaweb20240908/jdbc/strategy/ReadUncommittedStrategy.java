package org.yaremax.javaweb20240908.jdbc.strategy;

import java.sql.Connection;
import java.sql.SQLException;

public class ReadUncommittedStrategy implements IsolationLevelStrategy {
    @Override
    public void setTransactionIsolation(Connection connection) throws SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }
}
