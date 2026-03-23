package com.satyam.trading.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockSelectionService {

    // symbol → open price
    private final Map<String, Double> openPriceMap = new HashMap<>();

    // symbol → current price
    private final Map<String, Double> currentPriceMap = new HashMap<>();
    private List<String> cachedTopMovers = new ArrayList<>();
    private long lastUpdated = 0;

    public void updatePrice(String symbol, double price) {

        currentPriceMap.put(symbol, price);

        // set open price only once
        openPriceMap.putIfAbsent(symbol, price);
    }

    public List<String> getTopMovers() {

        if (currentPriceMap.size() < 20) {
            return Collections.emptyList();
        }

        long now = System.currentTimeMillis();
        // 🔥 update every 5 seconds
        if (now - lastUpdated < 5000) {
            return cachedTopMovers;
        }
        lastUpdated = now;
        cachedTopMovers = computeTopMovers();
        return cachedTopMovers;
    }

    public List<String> computeTopMovers() {
    Map<String, Double> changeMap = new HashMap<>();

        for (String symbol : currentPriceMap.keySet()) {

        double open = openPriceMap.getOrDefault(symbol, 0.0);
        double current = currentPriceMap.get(symbol);

        if (open == 0) continue;

        double changePercent = ((current - open) / open) * 100;

        changeMap.put(symbol, Math.abs(changePercent));
    }

        return changeMap.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(5) // 🔥 top 5 stocks
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
}
}