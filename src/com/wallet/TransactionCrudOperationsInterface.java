package com.wallet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface TransactionCrudOperationsInterface extends CrudOperations<Transaction> {
    List<Transaction> findByAccountId(UUID accountId);
    List<Transaction> findByDateRange(UUID accountId, Timestamp startDate, Timestamp endDate);
    Connection getConnection() throws SQLException;
}