package com.martin.aleksandrov.CashDeskModule.init;

import com.martin.aleksandrov.CashDeskModule.models.CashBalance;
import com.martin.aleksandrov.CashDeskModule.models.Cashier;
import com.martin.aleksandrov.CashDeskModule.models.enums.CurrencyType;
import com.martin.aleksandrov.CashDeskModule.service.CashService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Configuration
@AllArgsConstructor
public class CashInit implements CommandLineRunner {

    private static final String BALANCE_FILE = "BALANCES.txt";
    private final CashService cashService;

    @Override
    public void run(String... args) {

        Path path = Paths.get(BALANCE_FILE);
        boolean exists = Files.exists(path);

        if (!exists) {
            Cashier cashier = new Cashier();
            Map<String, Integer> bgn_balance = new HashMap<>();
            bgn_balance.put("10", 50);
            bgn_balance.put("50", 10);
            Map<String, Integer> eur_balance = new HashMap<>();
            eur_balance.put("10", 100);
            eur_balance.put("50", 20);

            cashier.setName("MARTINA");
            CashBalance cashBalanceBGN = CashBalance.builder()
                    .totalAmount(new BigDecimal(1000))
                    .currency(CurrencyType.BGN)
                    .denominations(bgn_balance)
                    .build();
            cashier.setCashBalance_BGN(cashBalanceBGN);
            CashBalance cashBalanceEUR = CashBalance.builder()
                    .totalAmount(new BigDecimal(2000))
                    .currency(CurrencyType.EUR)
                    .denominations(eur_balance)
                    .build();
            cashier.setCashBalance_EUR(cashBalanceEUR);

            this.cashService.saveBalances(BALANCE_FILE, cashier);
        }
    }
}
