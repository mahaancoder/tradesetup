//package com.satyam.trading.strategy;
//
//import com.satyam.trading.model.TradeSignal;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MomentumStrategy implements TradingStrategy {
//
//    @Override
//    public String getName() {
//        return "MomentumStrategy";
//    }
//
//    @Override
//    public TradeSignal onTick(String symbol, double price) {
//
//        if (price > 2500) {
//            return new TradeSignal(symbol, "BUY", price, getName(), price * 0.99);
//        }
//
//        return null;
//    }
//}