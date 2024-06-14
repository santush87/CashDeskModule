package com.martin.aleksandrov.CashDeskModule.models.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class CashOperationDto {

    private String type;
    private String currency;
    private Double amount;
    private Map<String, Double> denominations;
}
