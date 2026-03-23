package com.satyam.trading.strategy;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EMAService {

    private final Map<String, Double> emaMap = new HashMap<>();
    private final int period = 20;

    public double calculateEMA(String symbol, double price) {

        double multiplier = 2.0 / (period + 1);

        if (!emaMap.containsKey(symbol)) {
            emaMap.put(symbol, price);
            return price;
        }

        double prevEMA = emaMap.get(symbol);
        double ema = (price - prevEMA) * multiplier + prevEMA;

        emaMap.put(symbol, ema);

        return ema;
    }
}