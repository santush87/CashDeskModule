package com.martin.aleksandrov.CashDeskModule.service;

import com.martin.aleksandrov.CashDeskModule.exceptions.LowerThanZeroException;
import com.martin.aleksandrov.CashDeskModule.models.CashOperation;
import com.martin.aleksandrov.CashDeskModule.models.CashBalance;

import com.martin.aleksandrov.CashDeskModule.models.Cashier;
import com.martin.aleksandrov.CashDeskModule.models.enums.CurrencyType;
import com.martin.aleksandrov.CashDeskModule.models.enums.OperationType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.Map;


import java.util.*;

@Service
public class CashServiceImpl implements CashService {

    private static final String TRANSACTION_FILE = "TRANSACTIONS.txt";
    private static final String BALANCE_FILE = "BALANCES.txt";
//    private static final Map<String, CashBalance> balances = new HashMap<>();

    @Override
    public String processOperation(CashOperation cashOperation) {
        try {
            this.recordTransaction(cashOperation);
            this.updateBalance(cashOperation);
            return "Operation processed successfully.";
        } catch (IOException | LowerThanZeroException e) {
            return "Error processing operation: " + e.getMessage();
        }
    }


    private void recordTransaction(CashOperation cashOperation) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            writer.write(cashOperation.getType().name() + "; " + cashOperation.getCurrency().name() + "; " + cashOperation.getAmount() + ", "
                    + this.denominationsToString(cashOperation.getDenominations()) + System.lineSeparator());
        }
    }

    @Override
    public String denominationsToString(Map<Integer, Double> denominations) {
        StringBuilder sb = new StringBuilder();
        List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(denominations.entrySet());
        Iterator<Map.Entry<Integer, Double>> iterator = entryList.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Double> entry = iterator.next();
            sb.append(entry.getKey()).append("x").append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    private void updateBalance(CashOperation cashOperation) throws IOException, LowerThanZeroException {
        Cashier cashier = getCashier();

        String currency = cashOperation.getCurrency().name();

        CashBalance cashBalanceBgn = cashier.getCashBalance_BGN();
        CashBalance cashBalanceEur = cashier.getCashBalance_EUR();
//        CashBalance balance = cashier.get(cashOperation.getCurrency().name());

        if (cashOperation.getType().name().equals("DEPOSIT")) {
            if (currency.equals("BGN")) {
                cashBalanceBgn.setTotalAmount(cashBalanceBgn.getTotalAmount().add(cashOperation.getAmount()));
            }
            if (currency.equals("EUR")) {
                cashBalanceEur.setTotalAmount(cashBalanceEur.getTotalAmount().add(cashOperation.getAmount()));
            }
//            balance.setTotalAmount(balance.getTotalAmount().add(cashOperation.getAmount()));
        } else if (cashOperation.getType().name().equals("WITHDRAW")) {
            if (currency.equals("BGN")) {
                if (cashBalanceBgn.getTotalAmount().compareTo(cashOperation.getAmount()) < 0) {
                    throw new LowerThanZeroException("Not enough money!");
                }
                cashBalanceBgn.setTotalAmount(cashBalanceBgn.getTotalAmount().subtract(cashOperation.getAmount()));
            }
            if (currency.equals("EUR")) {
                if (cashBalanceEur.getTotalAmount().compareTo(cashOperation.getAmount()) < 0) {
                    throw new LowerThanZeroException("Not enough money!");
                }
                cashBalanceEur.setTotalAmount(cashBalanceEur.getTotalAmount().subtract(cashOperation.getAmount()));
            }

        }
        updateDenominations(cashOperation);
        saveBalances(BALANCE_FILE, cashier);
    }

    private void updateDenominations(CashOperation cashOperation) {
        Cashier cashier = getCashier();

        Map<Integer, Double> currentDenominations = null;
        Map<Integer, Double> operationDenominations = cashOperation.getDenominations();
        if (cashOperation.getCurrency().name().equals("EUR")) {
            currentDenominations = cashier.getCashBalance_EUR().getDenominations();
        } else if (cashOperation.getCurrency().name().equals("BGN")) {
            currentDenominations = cashier.getCashBalance_BGN().getDenominations();
        } else {
            throw new IllegalArgumentException("Unsupported currency: " + cashOperation.getCurrency());
        }

        if (cashOperation.getType().equals(OperationType.DEPOSIT)) {
            for (Map.Entry<Integer, Double> entry : operationDenominations.entrySet()) {
                currentDenominations.put(entry.getKey(), currentDenominations.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
        } else if (cashOperation.getType().equals(OperationType.WITHDRAW)) {
            for (Map.Entry<Integer, Double> entry : operationDenominations.entrySet()) {
                Integer denomination = entry.getKey();
                Double amount = entry.getValue();

                if (currentDenominations.containsKey(denomination)) {
                    Double currentAmount = currentDenominations.get(denomination);
                    if (currentAmount >= amount) {
                        currentDenominations.put(denomination, currentAmount - amount);
                        // Remove the denomination if the amount becomes zero
                        if (currentDenominations.get(denomination) == 0) {
                            currentDenominations.remove(denomination);
                        }
                    } else {
                        throw new IllegalArgumentException("Not enough money in denomination: " + denomination);
                    }
                } else {
                    throw new IllegalArgumentException("No such denomination available: " + denomination);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported operation type: " + cashOperation.getType());
        }

        // Update the cashier's denominations
        if (cashOperation.getCurrency().equals(CurrencyType.EUR)) {
            cashier.getCashBalance_EUR().setDenominations(currentDenominations);
        } else if (cashOperation.getCurrency().equals(CurrencyType.BGN)) {
            cashier.getCashBalance_BGN().setDenominations(currentDenominations);
        }

//        if (cashOperation.getType().name().equals("DEPOSIT")) {
//            for (Map.Entry<Integer, Double> entry : operationDenominations.entrySet()) {
//                currentDenominations.put(entry.getKey(), currentDenominations.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
//            }
//        } else if (cashOperation.getType().name().equals("WITHDRAW")) {
//            operationDenominations.forEach((k, v) -> {
//                if (currentDenominations.get(k) != null) {
//                    currentDenominations.put(k, currentDenominations.get(k) - v);
//                    if (currentDenominations.get(k) == 0) {
//                        currentDenominations.remove(k);
//                    } else if (currentDenominations.get(k) < 0) {
//                        throw new IllegalArgumentException("Not enough money!");
//                    }
//                } else {
//                    throw new IllegalArgumentException("There is no denomination with this operation!");
//                }
//            });
//        }
//        balance.setDenominations(currentDenominations);
        saveBalances(BALANCE_FILE, cashier);
    }

    @Override
    public void saveBalances(String balanceFile, Cashier cashier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(balanceFile))) {
            writer.write("Cashier: " + cashier.getName() + System.lineSeparator());
            writer.write(cashier.getCashBalance_BGN().getCurrency().name() + "; " + cashier.getCashBalance_BGN().getTotalAmount() + "; " +
                    denominationsToString(cashier.getCashBalance_BGN().getDenominations()) + System.lineSeparator());
            writer.write(cashier.getCashBalance_EUR().getCurrency().name() + "; " + cashier.getCashBalance_EUR().getTotalAmount() + "; " +
                    denominationsToString(cashier.getCashBalance_EUR().getDenominations()) + System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cashier getCashier() {
        Cashier cashier = new Cashier();
        ClassPathResource resource = new ClassPathResource(BALANCE_FILE);

        String firstLine;
        String secondLine;
        String thirdLine;

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            firstLine = reader.readLine();
            cashier.setName(firstLine.split(": ")[1]);

            secondLine = reader.readLine();
            this.setCashBalanceAndAmount(secondLine, CurrencyType.BGN, cashier);

            thirdLine = reader.readLine();
            this.setCashBalanceAndAmount(thirdLine, CurrencyType.EUR, cashier);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cashier;
    }

    private void setCashBalanceAndAmount(String line, CurrencyType currencyType, Cashier cashier) {
        BigDecimal amount = new BigDecimal(line.split("; ")[1]);

        String[] cashBal = line.split("; ")[2].split(", ");
        Map<Integer, Double> denominations = new HashMap<>();
        for (String s : cashBal) {
            String count = s.split("x")[0];
            String sum = s.split("x")[1];
            denominations.put(Integer.parseInt(count), Double.parseDouble(sum));
        }
        if (currencyType == CurrencyType.BGN) {
            cashier.getCashBalance_BGN().setTotalAmount(amount);
            cashier.getCashBalance_BGN().setDenominations(denominations);
        }
        if (currencyType == CurrencyType.EUR) {
            cashier.getCashBalance_EUR().setTotalAmount(amount);
            cashier.getCashBalance_EUR().setDenominations(denominations);
        }
    }

}
