package com.satyam.trading.controllers;

import com.satyam.trading.model.RiskMetrics;
import com.satyam.trading.risk.PortfolioRiskManager;
import com.satyam.trading.executionengine.ExecutionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RiskController {

    private final ExecutionEngine executionEngine;
    private final PortfolioRiskManager portfolioRiskManager;

    @GetMapping("/risk")
    public RiskMetrics getRisk() {

        double currentRisk = portfolioRiskManager.calculateCurrentRisk(
                executionEngine.getPositionMap()
        );

        double maxRisk = 50000; // example (5% of 10L)

        return new RiskMetrics(
                currentRisk,
                maxRisk,
                executionEngine.getTotalPnL(),
                executionEngine.getMaxDrawdown(),
                executionEngine.getOpenPositionCount()
        );
    }
}