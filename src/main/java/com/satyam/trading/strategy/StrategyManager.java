package com.satyam.trading.strategy;

import com.satyam.trading.model.TradeSignal;
import com.satyam.trading.executionengine.ExecutionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategyManager {

    private final List<TradingStrategy> strategies;
    private final ExecutionEngine executionEngine;

    public void processTick(String symbol, double price) throws Exception {

        LocalTime now = LocalTime.now();

        for (TradingStrategy strategy : strategies) {

            // 🔥 Time-based activation
            if (strategy instanceof ORBStrategy && now.isAfter(LocalTime.of(10, 30))) {
                continue;
            }

            if (strategy instanceof TrendPullbackStrategy && now.isBefore(LocalTime.of(10, 30))) {
                continue;
            }

            TradeSignal signal = strategy.onTick(symbol, price);

            if (signal != null) {
                executionEngine.processSignal(signal);
            }
        }
    }
}