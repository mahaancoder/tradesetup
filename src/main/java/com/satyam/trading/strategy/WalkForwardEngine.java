//package com.satyam.trading.strategy;
//
//import com.satyam.trading.backtest.BacktestEngine;
//import com.satyam.trading.model.Candle;
//import com.satyam.trading.model.Split;
//import com.satyam.trading.model.StrategyParams;
////import com.satyam.trading.service.ParameterOptimizer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class WalkForwardEngine {
//
////    private final ParameterOptimizer optimizer;
//    private final BacktestEngine backtestEngine;
//    private final ORBStrategy orbStrategy;
//
//    public void run(List<Candle> candles, String symbol) {
//
//        List<Split> splits = WalkForwardSplitter.split(candles, 100, 30);
//
//        double totalPnL = 0;
//
//        for (Split split : splits) {
//
//            System.out.println("---- NEW WINDOW ----");
//
//            // 🔥 Step 1: Optimize on TRAIN
//            StrategyParams bestParams = optimizer.findBestParams(split.getTrain(), symbol);
//
//            System.out.println("Best Params: " + bestParams);
//
//            // 🔥 Step 2: Apply params
//            orbStrategy.setParams(bestParams);
//
//            // 🔥 Step 3: Reset system
//            backtestEngine.reset();
//
//            // 🔥 Step 4: Test on UNSEEN data
//            backtestEngine.runBacktest(split.getTest(), symbol);
//
//            double pnl = backtestEngine.getTotalPnL();
//
//            totalPnL += pnl;
//
//            System.out.println("Test PnL: " + pnl);
//        }
//
//        System.out.println("🔥 WALK-FORWARD TOTAL PnL: " + totalPnL);
//    }
//}