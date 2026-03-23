//package com.satyam.trading.service;
//
//import com.satyam.trading.backtest.BacktestEngine;
//import com.satyam.trading.model.Candle;
//import com.satyam.trading.model.StrategyParams;
//import com.satyam.trading.strategy.ORBStrategy;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ParameterOptimizer {
//
//    private final BacktestEngine backtestEngine;
//    private final ORBStrategy orbStrategy;
//
//    public void optimize(List<Candle> candles, String symbol) {
//
//        List<StrategyParams> paramsList = ParameterGenerator.generate();
//
//        double bestPnL = Double.MIN_VALUE;
//        StrategyParams bestParams = null;
//
//        for (StrategyParams params : paramsList) {
//
//            // 🔥 Apply params
//            orbStrategy.setParams(params);
//
//            // 🔥 Reset system
//            backtestEngine.reset();
//
//            // 🔥 Run backtest
//            backtestEngine.runBacktest(candles, symbol);
//
//            double pnl = backtestEngine.getTotalPnL();
//
//            System.out.println("Params: " + params + " → PnL: " + pnl);
//
//            if (pnl > bestPnL) {
//                bestPnL = pnl;
//                bestParams = params;
//            }
//        }
//
//        System.out.println("🔥 BEST PARAMS: " + bestParams);
//        System.out.println("🔥 BEST PnL: " + bestPnL);
//    }
//}