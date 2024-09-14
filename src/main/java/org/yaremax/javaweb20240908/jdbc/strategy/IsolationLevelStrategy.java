package org.yaremax.javaweb20240908.jdbc.strategy;

import java.sql.Connection;
import java.sql.SQLException;


//TRANSACTION_READ_UNCOMMITTED
//        TRANSACTION_READ_COMMITTED
//TRANSACTION_REPEATABLE_READ
//        TRANSACTION_SERIALIZABLE

public interface IsolationLevelStrategy {
    void setTransactionIsolation(Connection connection) throws SQLException;
}
