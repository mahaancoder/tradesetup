package com.satyam.trading.controllers;

import com.satyam.trading.marketConnect.MarketDataService;
import com.satyam.trading.model.Position;
import com.satyam.trading.model.TradeHistory;
import com.satyam.trading.executionengine.ExecutionEngine;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Instrument;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DashboardController {

    private final ExecutionEngine executionEngine;
    private final MarketDataService marketDataService;
    private final String ACCESS_TOKEN = "cmbcya4fy9FF9ufsgesRXKwLioqe1uj6";

    @GetMapping("/positions")
    public List<Map<String, Object>> getPositions() {

        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, Map<String, Position>> positionMap = executionEngine.getPositionMap();

        for (Map.Entry<String, Map<String, Position>> symbolEntry : positionMap.entrySet()) {

            String symbol = symbolEntry.getKey();

            for (Map.Entry<String, Position> strategyEntry : symbolEntry.getValue().entrySet()) {

                Position p = strategyEntry.getValue();

                if (!p.isOpen()) continue;

                double currentPrice = p.getHighestPrice(); // replace later with live price
                double pnl = (currentPrice - p.getEntryPrice()) * p.getQuantity();

                result.add(Map.of(
                        "symbol", symbol,
                        "qty", p.getQuantity(),
                        "entry", p.getEntryPrice(),
                        "current", currentPrice,
                        "pnl", pnl
                ));
            }
        }

        return result;
    }

    @GetMapping("/holdings")
    public List<Map<String, Object>> getHoldings() {

        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, Map<String, Position>> positionMap = executionEngine.getPositionMap();

        for (Map<String, Position> strategyMap : positionMap.values()) {

            for (Position p : strategyMap.values()) {

                if (p.isOpen()) continue; // closed positions = holdings

                result.add(Map.of(
                        "symbol", p.getSymbol(),
                        "qty", p.getQuantity(),
                        "avgPrice", p.getEntryPrice()
                ));
            }
        }

        return result;
    }

    @GetMapping("/strategy-pnl")
    public Map<String, Double> getStrategyPnL() {
        return executionEngine.getStrategyPnL();
    }

    @GetMapping("/trades")
    public List<TradeHistory> getTrades() {
        return executionEngine.getTradeHistory();
    }

    @GetMapping("/instruments")
    public List<Instrument> getInstruments() throws KiteException, Exception {
       return marketDataService.getInstrumentsForExchange(ACCESS_TOKEN, "NSE");
    }
}