package com.satyam.trading.strategy;

import org.springframework.stereotype.Service;

@Service
public class StrategyService {

    public boolean shouldBuy(double price, double referencePrice) {
        return price > referencePrice;
    }

    public boolean shouldSell(double price, double entryPrice) {
        return price < entryPrice * 0.99; // 1% SL
    }
}