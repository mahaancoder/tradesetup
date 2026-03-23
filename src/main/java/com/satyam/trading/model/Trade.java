package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trade {

    private String symbol;
    private double entryPrice;
    private double exitPrice;
    private int quantity;
    private String type; // BUY / SELL
    private boolean open;
    private String strategy;
}