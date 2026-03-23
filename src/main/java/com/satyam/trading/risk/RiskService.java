package com.satyam.trading.risk;

import com.satyam.trading.model.TradeSignal;
import org.springframework.stereotype.Service;

@Service
public class RiskService {

    private static final double MAX_RISK_PER_TRADE = 0.01; // 1%
    private double dailyLoss = 0;
    private final double MAX_DAILY_LOSS = -2000;

    public int calculateQty(double capital, double price) {
        double riskAmount = capital * MAX_RISK_PER_TRADE;
        return (int) (riskAmount / price);
    }

    public boolean allowTrade(TradeSignal signal) {
        if (dailyLoss < MAX_DAILY_LOSS) {
            return false;
        }
        return true;
    }
}