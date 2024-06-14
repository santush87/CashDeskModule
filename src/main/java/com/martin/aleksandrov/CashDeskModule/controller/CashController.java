package com.martin.aleksandrov.CashDeskModule.controller;

import com.martin.aleksandrov.CashDeskModule.models.CashBalance;
import com.martin.aleksandrov.CashDeskModule.models.CashOperation;
import com.martin.aleksandrov.CashDeskModule.models.dtos.CashOperationDto;
import com.martin.aleksandrov.CashDeskModule.service.CashService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/cash-operation")
    public ResponseEntity<String> getCashOperation(@RequestHeader("FIB-X-AUTH") String apiKey,
                                                   @RequestBody CashOperationDto cashOperationDto) {
        if (!"f9Uie8nNf112hX8s".equals(apiKey)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        try {
            String balances = this.cashService.processOperation(cashOperationDto);
            return new ResponseEntity<>(balances, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/cash-balance")
//    public ResponseEntity<CashBalance[]> getCashBalance(@RequestHeader("FIB-X-AUTH") String apiKey) {
//        if (!"f9Uie8nNf112hX8s".equals(apiKey)) {
//            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//        }
//        CashBalance[] balances = cashService.getBalances();
//        return new ResponseEntity<>(balances, HttpStatus.OK);
//    }
}
