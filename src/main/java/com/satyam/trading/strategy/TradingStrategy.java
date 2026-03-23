package com.satyam.trading.strategy;

import com.satyam.trading.model.TradeSignal;

public interface TradingStrategy {

    String getName();

    TradeSignal onTick(String symbol, double price);

}