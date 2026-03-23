package com.satyam.trading.controllers;

import com.satyam.trading.strategy.InstrumentRegistry;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for managing instruments
 */
@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentLoaderService instrumentLoaderService;

    /**
     * Load NSE Equity instruments into the registry
     * GET /api/instruments/load?accessToken=YOUR_TOKEN
     */
    @GetMapping("/load")
    public Map<Long, String> loadNSEInstruments(@RequestParam String accessToken) throws Exception, KiteException {
        return instrumentLoaderService.loadNSEEquityInstruments(accessToken);
        
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "NSE Equity instruments loaded successfully");
//        response.put("count", count);
//        response.put("totalInRegistry", instrumentLoaderService.getInstrumentCount());
//
//        return response;
    }

    /**
     * Load instruments from multiple exchanges
     * GET /api/instruments/load-multiple?accessToken=YOUR_TOKEN&exchanges=NSE,BSE
     */
    @GetMapping("/load-multiple")
    public Map<String, Object> loadMultipleExchanges(
            @RequestParam String accessToken,
            @RequestParam String exchanges) throws Exception, KiteException {
        
        String[] exchangeArray = exchanges.split(",");
        int count = instrumentLoaderService.loadEquityInstruments(accessToken, exchangeArray);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Equity instruments loaded successfully");
        response.put("exchanges", exchangeArray);
        response.put("count", count);
        response.put("totalInRegistry", instrumentLoaderService.getInstrumentCount());
        
        return response;
    }

    /**
     * Get current instrument count
     * GET /api/instruments/count
     */
    @GetMapping("/count")
    public Map<String, Object> getCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", instrumentLoaderService.getInstrumentCount());
        return response;
    }

    /**
     * Clear all instruments from registry
     * DELETE /api/instruments/clear
     */
    @DeleteMapping("/clear")
    public Map<String, Object> clearInstruments() {
        instrumentLoaderService.clearInstruments();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All instruments cleared from registry");
        response.put("count", 0);
        
        return response;
    }

    /**
     * Get a sample of loaded instruments (first 10)
     * GET /api/instruments/sample
     */
    @GetMapping("/sample")
    public Map<String, Object> getSample() {
        Map<String, Object> response = new HashMap<>();
        Map<Long, String> sample = new HashMap<>();
        
        int count = 0;
        for (Map.Entry<Long, String> entry : InstrumentRegistry.tokenToSymbol.entrySet()) {
            if (count >= 10) break;
            sample.put(entry.getKey(), entry.getValue());
            count++;
        }
        
        response.put("sample", sample);
        response.put("totalCount", instrumentLoaderService.getInstrumentCount());
        
        return response;
    }

    /**
     * Search for a specific symbol
     * GET /api/instruments/search?symbol=RELIANCE
     */
    @GetMapping("/search")
    public Map<String, Object> searchSymbol(@RequestParam String symbol) {
        Map<String, Object> response = new HashMap<>();
        
        for (Map.Entry<Long, String> entry : InstrumentRegistry.tokenToSymbol.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(symbol)) {
                response.put("found", true);
                response.put("token", entry.getKey());
                response.put("symbol", entry.getValue());
                return response;
            }
        }
        
        response.put("found", false);
        response.put("message", "Symbol not found in registry");
        return response;
    }
}

