//package com.satyam.trading.service;
//
//import com.satyam.trading.marketdata.MarketDataService;
//import com.satyam.trading.risk.RiskService;
//import com.satyam.trading.strategy.StrategyService;
//import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class TradingScheduler {
//
//    private final MarketDataService marketDataService;
//    private final StrategyService strategyService;
//    private final RiskService riskService;
//    private final PaperTradingService paperTradingService;
//
//    private final String ACCESS_TOKEN = "X7hKVMF2fQfrF9pN0XZFQqBFYj8g9lh5";
//
//    private boolean positionOpen = false;
//    private double entryPrice = 0;
//
////    @Scheduled(fixedDelay = 5000)
////    public void run() throws Exception, KiteException {
////        int qty = 1;
////        double price = marketDataService.getLtp(ACCESS_TOKEN, "NSE:RELIANCE");
////
////        if (!positionOpen) {
////            if (strategyService.shouldBuy(price, 2500)) {
////
////                qty = riskService.calculateQty(100000, price);
////                paperTradingService.buy("RELIANCE", price, qty, "random");
////
////                entryPrice = price;
////                positionOpen = true;
////            }
////        } else {
////            if (strategyService.shouldSell(price, entryPrice)) {
////
////                paperTradingService.sell("RELIANCE", price, qty, "random");
////                positionOpen = false;
////            }
////        }
////
////        System.out.println("Total PnL: " + paperTradingService.getPnl());
////    }
//}