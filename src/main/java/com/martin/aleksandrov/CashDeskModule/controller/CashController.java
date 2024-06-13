package com.martin.aleksandrov.CashDeskModule.controller;

import com.martin.aleksandrov.CashDeskModule.service.CashService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CashController {

    private final CashService cashService;

//    @GetMapping("/cash-operation")
//    public ResponseEntity<CashBalance[]> getCashOperation(@RequestHeader("FIB-X-AUTH") String apiKey) {
//        if (!"f9Uie8nNf112hX8s".equals(apiKey)) {
//            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//        }
////        CashBalance[] balances = cashService.getBalances();
//        return new ResponseEntity<>(balances, HttpStatus.OK);
//    }

//    @GetMapping("/cash-balance")
//    public ResponseEntity<CashBalance[]> getCashBalance(@RequestHeader("FIB-X-AUTH") String apiKey) {
//        if (!"f9Uie8nNf112hX8s".equals(apiKey)) {
//            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//        }
//        CashBalance[] balances = cashService.getBalances();
//        return new ResponseEntity<>(balances, HttpStatus.OK);
//    }
}
