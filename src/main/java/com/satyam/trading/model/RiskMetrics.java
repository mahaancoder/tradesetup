package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskMetrics {

    private double currentRisk;
    private double maxRisk;
    private double dailyPnL;
    private double maxDrawdown;
    private int openPositions;
}