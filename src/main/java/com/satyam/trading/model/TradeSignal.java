package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TradeSignal {

    private String symbol;
    private String action; // BUY / SELL
    private double price;
    private String strategyName;
    private double stopLoss;
}