package com.satyam.trading.backtest;

import com.satyam.trading.model.Candle;
import com.satyam.trading.executionengine.ExecutionEngine;
import com.satyam.trading.strategy.StrategyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BacktestEngine {

    private final StrategyManager strategyManager;
    private final ExecutionEngine executionEngine;

    public void runBacktest(List<Candle> candles, String symbol) throws Exception {

        for (Candle candle : candles) {

            double price = candle.getClose();

            // simulate tick
            executionEngine.updatePrice(symbol, price);
            executionEngine.evaluateExit(symbol, price);
            strategyManager.processTick(symbol, price);
        }

        printResults();
    }

    private void printResults() {
        System.out.println("Total PnL: " + executionEngine.getTotalPnL());
    }
}