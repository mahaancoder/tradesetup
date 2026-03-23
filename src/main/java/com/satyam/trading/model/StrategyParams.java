package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StrategyParams {

    private double breakoutBuffer;
    private double atrMultiplier;
    private int emaPeriod;
    private double pullbackThreshold;
}