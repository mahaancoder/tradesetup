package com.satyam.trading.capitalallocation;

import org.springframework.stereotype.Service;

@Service
public class CapitalAllocationService {

    private double accountSize = 1_000_000; // ₹10L
    private double riskPerTradePercent = 0.01; // 1%

    public int calculateQty(double entryPrice, double stopLoss) {

        double riskAmount = accountSize * riskPerTradePercent;

        double riskPerUnit = Math.abs(entryPrice - stopLoss);

        if (riskPerUnit == 0) return 0;

        int qty = (int) (riskAmount / riskPerUnit);

        return Math.max(qty, 1);
    }
}