package com.martin.aleksandrov.CashDeskModule.service;

import com.martin.aleksandrov.CashDeskModule.models.CashOperation;
import com.martin.aleksandrov.CashDeskModule.models.Cashier;

import java.util.Map;

public interface CashService {

    String processOperation(CashOperation cashOperation);
    String denominationsToString(Map<Integer, Double> denominations);
    void saveBalances(String balanceFile, Cashier cashier);
}
