package com.martin.aleksandrov.CashDeskModule.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cashier {
    private String name;
    private CashBalance cashBalance_BGN;
    private CashBalance cashBalance_EUR;
}
