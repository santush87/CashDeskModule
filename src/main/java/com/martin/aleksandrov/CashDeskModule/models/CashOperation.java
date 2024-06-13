package com.martin.aleksandrov.CashDeskModule.models;

import com.martin.aleksandrov.CashDeskModule.models.enums.CurrencyType;
import com.martin.aleksandrov.CashDeskModule.models.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashOperation {

    private OperationType type;
    private CurrencyType currency;
    private BigDecimal amount;
    private Map<Integer, Double> denominations;
}
