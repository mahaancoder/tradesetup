package com.satyam.trading.risk;

import com.satyam.trading.model.Position;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PortfolioRiskManager {

    private double accountSize = 1_000_000; // ₹10L
    private double maxPortfolioRiskPercent = 0.05; // 5%

    public boolean canTakeTrade(Map<String, Map<String, Position>> positionMap,
                                double newTradeRisk) {

        double currentRisk = calculateCurrentRisk(positionMap);

        double maxAllowedRisk = accountSize * maxPortfolioRiskPercent;

        if (currentRisk + newTradeRisk > maxAllowedRisk) {
            System.out.println("❌ Portfolio risk exceeded");
            return false;
        }

        return true;
    }

    public double calculateCurrentRisk(Map<String, Map<String, Position>> positionMap) {

        double totalRisk = 0;

        for (Map<String, Position> strategyMap : positionMap.values()) {

            for (Position position : strategyMap.values()) {

                if (!position.isOpen()) continue;

                double riskPerUnit = Math.abs(position.getEntryPrice() - position.getStopLoss());

                totalRisk += riskPerUnit * position.getQuantity();
            }
        }

        return totalRisk;
    }
}