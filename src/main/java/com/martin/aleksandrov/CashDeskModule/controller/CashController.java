package com.martin.aleksandrov.CashDeskModule.controller;

import com.martin.aleksandrov.CashDeskModule.config.AppConfig;
import com.martin.aleksandrov.CashDeskModule.models.dtos.CashOperationDto;
import com.martin.aleksandrov.CashDeskModule.service.CashService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CashController {

    private final CashService cashService;
    private final String API_KEY;

    public CashController(CashService cashService, AppConfig appConfig) {
        this.cashService = cashService;
        this.API_KEY = appConfig.getApiKey();
    }

    @PostMapping("/cash-operation")
    public ResponseEntity<String> getCashOperation(@RequestHeader("FIB-X-AUTH") String apiKey,
                                                   @RequestBody CashOperationDto cashOperationDto) {
        if (!API_KEY.equals(apiKey)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        try {
            String result = this.cashService.processOperation(cashOperationDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cash-balance")
    public ResponseEntity<String> getCashBalance(@RequestHeader("FIB-X-AUTH") String apiKey) {
        if (!API_KEY.equals(apiKey)) {
            return new ResponseEntity<>("Unauthorized! Wrong api key!", HttpStatus.UNAUTHORIZED);
        }
        String balances = cashService.getBalances();
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }
}
