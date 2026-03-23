package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TradeHistory {

    private String symbol;
    private String strategy;
    private double entry;
    private double exit;
    private int quantity;
    private double pnl;
    private long timestamp;
}