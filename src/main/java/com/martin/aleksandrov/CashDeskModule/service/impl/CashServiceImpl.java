package com.martin.aleksandrov.CashDeskModule.service.impl;

import com.martin.aleksandrov.CashDeskModule.exceptions.LowerThanZeroException;
import com.martin.aleksandrov.CashDeskModule.models.CashOperation;
import com.martin.aleksandrov.CashDeskModule.models.CashBalance;

import com.martin.aleksandrov.CashDeskModule.models.Cashier;
import com.martin.aleksandrov.CashDeskModule.models.dtos.CashOperationDto;
import com.martin.aleksandrov.CashDeskModule.models.enums.CurrencyType;
import com.martin.aleksandrov.CashDeskModule.service.CashService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.Map;


import java.util.*;

@Service
@AllArgsConstructor
public class CashServiceImpl implements CashService {

    private static final String TRANSACTION_FILE = "TRANSACTIONS.txt";
    private static final String BALANCE_FILE = "BALANCES.txt";
    private final ModelMapper modelMapper;

    @Override
    public String processOperation(CashOperationDto cashOperationDto) throws LowerThanZeroException, IOException {
        CashOperation cashOperation = this.modelMapper.map(cashOperationDto, CashOperation.class);

        if (cashOperation.getCurrency() == null) {
            throw new IllegalArgumentException("Invalid currency");
        }
        if (cashOperation.getType() == null) {
            throw new IllegalArgumentException("Invalid type");
        }
        if (cashOperation.getAmount() == null) {
            throw new IllegalArgumentException("Invalid amount");
        }
        if (cashOperation.getDenominations().isEmpty()) {
            throw new IllegalArgumentException("Invalid denominations");
        }

        BigDecimal totalSum = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : cashOperation.getDenominations().entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            BigDecimal keyAsBigDecimal = new BigDecimal(key);
            BigDecimal product = keyAsBigDecimal.multiply(new BigDecimal(value));

            totalSum = totalSum.add(product);
        }

        if (cashOperation.getAmount().compareTo(totalSum) != 0) {
            throw new IllegalArgumentException("The amount is not equal to the total sum from denominations");
        }


        this.updateData(cashOperation);
        this.recordTransaction(cashOperation);
        return "Operation processed successfully.";
    }


    private void recordTransaction(CashOperation cashOperation) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            writer.write(cashOperation.getType().name() + "; " + cashOperation.getCurrency().name() + "; " + cashOperation.getAmount() + ", "
                    + this.denominationsToString(cashOperation.getDenominations()) + System.lineSeparator());
        }
    }

    @Override
    public String denominationsToString(Map<String, Integer> denominations) {
        StringBuilder denominationBuilder = new StringBuilder();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(denominations.entrySet());
        Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            denominationBuilder.append(entry.getValue()).append("x").append(entry.getKey());
            if (iterator.hasNext()) {
                denominationBuilder.append(", ");
            }
        }
        return denominationBuilder.toString();
    }


    private void updateData(CashOperation cashOperation) throws LowerThanZeroException {
        Cashier cashier = getCashier();

        String currency = cashOperation.getCurrency().name();

        CashBalance cashBalanceBgn = cashier.getCashBalance_BGN();
        CashBalance cashBalanceEur = cashier.getCashBalance_EUR();

        Map<String, Integer> currentDenominations;
        Map<String, Integer> operationDenominations = cashOperation.getDenominations();

        if (currency.equals("EUR")) {
            currentDenominations = cashBalanceEur.getDenominations();
        } else if (currency.equals("BGN")) {
            currentDenominations = cashBalanceBgn.getDenominations();
        } else {
            throw new IllegalArgumentException("Unsupported currency: " + cashOperation.getCurrency());
        }

//        DEPOSIT OPERATION
        if (cashOperation.getType().name().equals("DEPOSIT")) {
            if (currency.equals("BGN")) {
                cashBalanceBgn.setTotalAmount(cashBalanceBgn.getTotalAmount().add(cashOperation.getAmount()));
                for (Map.Entry<String, Integer> entry : operationDenominations.entrySet()) {
                    cashBalanceBgn.getDenominations().merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
            if (currency.equals("EUR")) {
                cashBalanceEur.setTotalAmount(cashBalanceEur.getTotalAmount().add(cashOperation.getAmount()));
                for (Map.Entry<String, Integer> entry : operationDenominations.entrySet()) {
                    cashBalanceEur.getDenominations().merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
            for (Map.Entry<String, Integer> entry : operationDenominations.entrySet()) {
                operationDenominations.put(entry.getKey(), operationDenominations.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }

//            WITHDRAW OPERATION
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

            for (Map.Entry<String, Integer> entry : operationDenominations.entrySet()) {
                String banknote = entry.getKey();
                Integer banknoteCount = entry.getValue();

                if (currentDenominations.containsKey(banknote)) {
                    Integer currentAmount = currentDenominations.get(banknote);
                    if (currentAmount >= banknoteCount) {
                        currentDenominations.put(banknote, currentAmount - banknoteCount);
                        // Remove the banknote if the banknoteCount becomes zero
                        if (currentDenominations.get(banknote) == 0) {
                            currentDenominations.remove(banknote);
                        }
                    } else {
                        throw new IllegalArgumentException("Not enough money in banknote: " + banknote);
                    }
                } else {
                    throw new IllegalArgumentException("No such banknote available: " + banknote);
                }
            }
        }
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
        CashBalance bgnCash = new CashBalance();
        bgnCash.setCurrency(CurrencyType.BGN);

        CashBalance eurCash = new CashBalance();
        eurCash.setCurrency(CurrencyType.EUR);

        cashier.setCashBalance_BGN(bgnCash);
        cashier.setCashBalance_EUR(eurCash);

        String firstLine;
        String secondLine;
        String thirdLine;

        try (BufferedReader reader = new BufferedReader(new FileReader(BALANCE_FILE))) {
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

        Map<String, Integer> denominations = new HashMap<>();

        String[] parts = line.split("; ");
        if (parts.length == 3) {
            String[] cashBal = parts[2].split(", ");
            for (String s : cashBal) {
                String banknoteCount = s.split("x")[0];
                String banknote = s.split("x")[1];
                denominations.put(banknote, Integer.parseInt(banknoteCount));
            }
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

    @Override
    public String getBalances() {
        Cashier cashier = getCashier();
        return "Cashier: " + cashier.getName() + System.lineSeparator() +
                cashier.getCashBalance_BGN().getCurrency().name() + "; " + cashier.getCashBalance_BGN().getTotalAmount() + "; " +
                denominationsToString(cashier.getCashBalance_BGN().getDenominations()) + System.lineSeparator() +
                cashier.getCashBalance_EUR().getCurrency().name() + "; " + cashier.getCashBalance_EUR().getTotalAmount() + "; " +
                denominationsToString(cashier.getCashBalance_EUR().getDenominations()) + System.lineSeparator();
    }
}
