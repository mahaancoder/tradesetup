package com.satyam.trading.strategy;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VWAPService {

    private final Map<String, Double> totalPriceVolume = new HashMap<>();
    private final Map<String, Double> totalVolume = new HashMap<>();

    public double calculateVWAP(String symbol, double price, double volume) {

        totalPriceVolume.put(symbol,
                totalPriceVolume.getOrDefault(symbol, 0.0) + price * volume);

        totalVolume.put(symbol,
                totalVolume.getOrDefault(symbol, 0.0) + volume);

        return totalPriceVolume.get(symbol) / totalVolume.get(symbol);
    }
}