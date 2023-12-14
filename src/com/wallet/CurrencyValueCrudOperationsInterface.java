package com.wallet;

import java.sql.Timestamp;
import java.util.List;

public interface CurrencyValueCrudOperationsInterface extends CrudOperations<CurrencyValue> {
    List<CurrencyValue> findByDateEffect(Timestamp dateEffect);
}
