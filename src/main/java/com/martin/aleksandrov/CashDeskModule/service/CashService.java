package com.martin.aleksandrov.CashDeskModule.service;

import com.martin.aleksandrov.CashDeskModule.exceptions.LowerThanZeroException;
import com.martin.aleksandrov.CashDeskModule.models.Cashier;
import com.martin.aleksandrov.CashDeskModule.models.dtos.CashOperationDto;

import java.io.IOException;
import java.util.Map;

public interface CashService {

    String processOperation(CashOperationDto cashOperationDto) throws IOException, LowerThanZeroException;
    String denominationsToString(Map<String, Integer> denominations);
    void saveBalances(String balanceFile, Cashier cashier);
    String getBalances();
}
