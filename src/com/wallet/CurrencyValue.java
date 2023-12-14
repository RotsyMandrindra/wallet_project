package com.wallet;

import java.sql.Timestamp;
import java.util.UUID;

public class CurrencyValue {
    private UUID idCurrencyValue;
    private UUID idCurrency;
    private UUID idCurrencyDestination;
    private Double amount;
    private Timestamp dateEffect;

    public CurrencyValue(UUID idCurrencyValue, UUID idCurrency, UUID idCurrencyDestination, Double amount, Timestamp dateEffect) {
        this.idCurrencyValue = idCurrencyValue;
        this.idCurrency = idCurrency;
        this.idCurrencyDestination = idCurrencyDestination;
        this.amount = amount;
        this.dateEffect = dateEffect;
    }

    public UUID getIdCurrencyValue() {
        return idCurrencyValue;
    }

    public void setIdCurrencyValue(UUID idCurrencyValue) {
        this.idCurrencyValue = idCurrencyValue;
    }

    public UUID getIdCurrency() {
        return idCurrency;
    }

    public void setIdCurrency(UUID idCurrency) {
        this.idCurrency = idCurrency;
    }

    public UUID getIdCurrencyDestination() {
        return idCurrencyDestination;
    }

    public void setIdCurrencyDestination(UUID idCurrencyDestination) {
        this.idCurrencyDestination = idCurrencyDestination;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getDateEffect() {
        return dateEffect;
    }

    public void setDateEffect(Timestamp dateEffect) {
        this.dateEffect = dateEffect;
    }
}
