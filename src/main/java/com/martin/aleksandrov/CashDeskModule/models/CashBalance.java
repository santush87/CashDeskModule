package com.martin.aleksandrov.CashDeskModule.models;

import com.martin.aleksandrov.CashDeskModule.models.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashBalance {

    private CurrencyType currency;
    private BigDecimal totalAmount;
    private Map<String, Integer> denominations;
}
