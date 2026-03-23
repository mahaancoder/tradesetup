package com.satyam.trading.strategy;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class MarketTrendService {

    private static final int PERIOD = 20;

    private final Queue<Double> priceQueue = new LinkedList<>();
    private double sum = 0;

    private double lastPrice = 0;

    public void updatePrice(double price) {

        lastPrice = price;

        priceQueue.add(price);
        sum += price;

        if (priceQueue.size() > PERIOD) {
            sum -= priceQueue.poll();
        }
    }

    public boolean isBullish() {

        if (priceQueue.size() < PERIOD) return true; // neutral

        double ema = sum / PERIOD;

        return lastPrice > ema;
    }
}