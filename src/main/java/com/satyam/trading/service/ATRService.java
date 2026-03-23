package com.satyam.trading.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ATRService {

    private static final int PERIOD = 14;

    private final Map<String, Queue<Double>> trMap = new HashMap<>();
    private final Map<String, Double> prevCloseMap = new HashMap<>();

    public double calculateATR(String symbol, double high, double low, double close) {

        double prevClose = prevCloseMap.getOrDefault(symbol, close);

        double tr = Math.max(high - low,
                Math.max(Math.abs(high - prevClose), Math.abs(low - prevClose)));

        prevCloseMap.put(symbol, close);

        trMap.putIfAbsent(symbol, new LinkedList<>());
        Queue<Double> queue = trMap.get(symbol);

        queue.add(tr);
        if (queue.size() > PERIOD) {
            queue.poll();
        }

        return queue.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
}