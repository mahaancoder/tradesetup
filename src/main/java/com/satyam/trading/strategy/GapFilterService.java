package com.satyam.trading.strategy;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GapFilterService {

    private final Map<String, Double> prevCloseMap = new HashMap<>();
    private final Map<String, Double> todayOpenMap = new HashMap<>();

    public void setPrevClose(String symbol, double close) {
        prevCloseMap.put(symbol, close);
    }

    public void setTodayOpen(String symbol, double open) {
        todayOpenMap.putIfAbsent(symbol, open);
    }

    public boolean isValidGap(String symbol) {

        double prevClose = prevCloseMap.getOrDefault(symbol, 0.0);
        double open = todayOpenMap.getOrDefault(symbol, 0.0);

        if (prevClose == 0 || open == 0) return true;

        double gapPercent = Math.abs((open - prevClose) / prevClose) * 100;

        return gapPercent < 2.0; // 🔥 threshold
    }
}