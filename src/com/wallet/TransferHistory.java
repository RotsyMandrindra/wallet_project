package com.wallet;

import java.sql.Timestamp;
import java.util.UUID;

public class TransferHistory {
    private UUID id_transfer_history;
    private UUID debit_transaction_id;
    private UUID credit_transaction_id;
    private Timestamp transfer_date;
    private double amount;
    public TransferHistory(UUID id_transfer_history, UUID debit_transaction_id, UUID credit_transaction_id, Timestamp transfer_date, double amount) {
        this.id_transfer_history = id_transfer_history;
        this.debit_transaction_id = debit_transaction_id;
        this.credit_transaction_id = credit_transaction_id;
        this.transfer_date = transfer_date;
        this.amount = amount;
    }

    public UUID getId_transfer_history() {
        return id_transfer_history;
    }

    public void setId_transfer_history(UUID id_transfer_history) {
        this.id_transfer_history = id_transfer_history;
    }

    public UUID getDebit_transaction_id() {
        return debit_transaction_id;
    }

    public void setDebit_transaction_id(UUID debit_transaction_id) {
        this.debit_transaction_id = debit_transaction_id;
    }

    public UUID getCredit_transaction_id() {
        return credit_transaction_id;
    }

    public void setCredit_transaction_id(UUID credit_transaction_id) {
        this.credit_transaction_id = credit_transaction_id;
    }

    public Timestamp getTransfer_date() {
        return transfer_date;
    }

    public void setTransfer_date(Timestamp transfer_date) {
        this.transfer_date = transfer_date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
