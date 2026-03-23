package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
    private String symbol;
    private int quantity;
    private double entryPrice;
    private double stopLoss;
    private double target;
    private double highestPrice; // 🔥 for trailing SL
    private boolean open;
    private boolean partialBooked;
}