package com.satyam.trading.executionengine;

import com.satyam.trading.authentication.TokenStore;
import com.satyam.trading.capitalallocation.CapitalAllocationService;
import com.satyam.trading.model.Position;
import com.satyam.trading.model.TradeHistory;
import com.satyam.trading.order.OrderService;
import com.satyam.trading.risk.PortfolioRiskManager;
import com.satyam.trading.risk.RiskService;
import com.satyam.trading.model.TradeSignal;
import com.satyam.trading.service.ATRService;
import com.satyam.trading.service.PaperTradingService;
import com.satyam.trading.websocket.LiveDataHandler;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExecutionEngine {

    private final RiskService riskService;
    private final PaperTradingService paperTradingService;
    private final OrderService orderService;
    private boolean paperMode = true;
    private final ATRService atrService;
    private final CapitalAllocationService capitalAllocationService;
    private final PortfolioRiskManager portfolioRiskManager;
    private static final double RISK_PER_TRADE = 10000; // ₹10k
    @Getter
    private double totalPnL = 0;
    // 🔥 Position tracking (symbol → position)
    @Getter
    private final Map<String, Map<String, Position>> positionMap = new HashMap<>();
    private final TokenStore tokenStore;
    private final Map<String, Double> latestPriceMap = new HashMap<>();
    private final LiveDataHandler liveDataHandler;
    @Getter
    private final Map<String, Double> strategyPnL = new HashMap<>();
    @Getter
    private final List<TradeHistory> tradeHistory = new ArrayList<>();
    private double peakPnL = 0;
    @Getter
    private double maxDrawdown = 0;
    public void updatePrice(String symbol, double price) {
        latestPriceMap.put(symbol, price);
    }

    public double getLatestPrice(String symbol) {
        return latestPriceMap.getOrDefault(symbol, 0.0);
    }

    public void processSignal(TradeSignal signal) throws Exception {

        String symbol = signal.getSymbol();
        String strategy = signal.getStrategyName();

        // ✅ Step 1: Risk check
        if (!riskService.allowTrade(signal)) {
            System.out.println("❌ Risk rejected: " + signal);
            return;
        }

        // ✅ Step 2: Get strategy-specific position
        positionMap.putIfAbsent(symbol, new HashMap<>());
        Map<String, Position> strategyPositions = positionMap.get(symbol);

        Position position = strategyPositions.get(strategy);

        // ✅ Step 3: Action handling
        if ("BUY".equals(signal.getAction())) {

            if (position != null && position.isOpen()) {
                // already open → ignore
                return;
            }

            executeBuy(signal, strategyPositions);

        } else if ("SELL".equals(signal.getAction())) {

            if (position == null || !position.isOpen()) {
                // nothing to sell → ignore
                return;
            }

            executeSell(signal, position, strategyPositions);
        }
    }

    // 🔥 BUY
    private void executeBuy(TradeSignal signal,
                            Map<String, Position> strategyPositions) throws Exception {

        double entry = signal.getPrice();
        double stopLoss = signal.getStopLoss(); // ✅ USE STRATEGY SL

        // ❗ Safety check
        if (stopLoss <= 0 || entry <= stopLoss) {
            System.out.println("❌ Invalid SL for " + signal.getSymbol());
            return;
        }

        // 🔥 Optional: calculate target (RR = 2)
        double riskPerUnit = entry - stopLoss;
        double target = entry + (riskPerUnit * 2);

        // 🔥 Position sizing
        int qty = capitalAllocationService.calculateQty(entry, stopLoss);

        if (qty <= 0) {
            System.out.println("❌ Quantity is 0 for " + signal.getSymbol());
            return;
        }

        // 🔥 Portfolio risk check
        double tradeRisk = riskPerUnit * qty;

        if (!portfolioRiskManager.canTakeTrade(positionMap, tradeRisk)) {
            System.out.println("❌ Trade rejected due to portfolio risk");
            return;
        }

        // 🔥 Execute trade
        if (paperMode) {
            paperTradingService.buy(signal.getSymbol(), entry, qty, signal.getStrategyName());
        } else {
            try {
                orderService.placeBuyOrder(
                        tokenStore.getAccessToken(),
                        signal.getSymbol(),
                        qty
                );
            } catch (Exception | KiteException e) {
                System.out.println("❌ Buy failed: " + e.getMessage());
                return;
            }
        }

        // 🔥 Save position
        Position position = new Position(
                signal.getSymbol(),
                qty,
                entry,
                stopLoss,
                target,
                entry,   // highestPrice
                true,
                false
        );

        strategyPositions.put(signal.getStrategyName(), position);

        // 🔥 Logging
        liveDataHandler.broadcast(
                "{ \"type\":\"LOG\", \"message\":\"BUY " +
                        signal.getSymbol() +
                        " Qty=" + qty +
                        " @ " + entry + "\"}"
        );

        System.out.println("🟢 BUY " + signal.getSymbol()
                + " Qty: " + qty
                + " Entry: " + entry
                + " SL: " + stopLoss
                + " Target: " + target);

        // 🔥 Update dashboard
        broadcastRisk();
    }

    // 🔥 SELL
    private void executeSell(TradeSignal signal,
                             Position position,
                             Map<String, Position> strategyPositions) throws Exception {

        String symbol = signal.getSymbol();
        double exitPrice = signal.getPrice();

        int qty = position.getQuantity();
        double entry = position.getEntryPrice();

        // ❗ Safety
        if (!position.isOpen() || qty <= 0) {
            return;
        }

        // 🔥 Execute sell (paper/live)
        if (paperMode) {
            paperTradingService.sell(symbol, exitPrice, qty, signal.getStrategyName());
        } else {
            try {
                orderService.placeSellOrder(
                        tokenStore.getAccessToken(),
                        symbol,
                        qty
                );
            } catch (Exception | KiteException e) {
                System.out.println("❌ Sell failed: " + e.getMessage());
                return;
            }
        }

        // 🔥 Calculate PnL
        double pnl = (exitPrice - entry) * qty;
        totalPnL += pnl;

        // 🔥 Mark position closed
        position.setOpen(false);

        // 🔥 Remove from map
        strategyPositions.remove(signal.getStrategyName());

        // 🔥 Track trade history
        tradeHistory.add(new TradeHistory(
                symbol,
                signal.getStrategyName(),
                entry,
                exitPrice,
                qty,
                pnl,
                System.currentTimeMillis()
        ));
        liveDataHandler.broadcast(
                "{ \"type\":\"TRADE\", " +
                        "\"symbol\":\"" + symbol + "\"," +
                        "\"strategy\":\"" + signal.getStrategyName() + "\"," +
                        "\"entry\":" + entry + "," +
                        "\"exit\":" + exitPrice + "," +
                        "\"qty\":" + qty + "," +
                        "\"pnl\":" + pnl + "}"
        );
        // 🔥 Strategy-wise PnL
        strategyPnL.put(
                signal.getStrategyName(),
                strategyPnL.getOrDefault(signal.getStrategyName(), 0.0) + pnl
        );

        String message = "🔴 SELL " + symbol +
                " Qty=" + qty +
                " Exit=" + exitPrice +
                " PnL=" + pnl;
        // 🔥 Logs
        System.out.println(message);

        liveDataHandler.broadcast(
                "{ \"type\":\"LOG\", \"message\":\"" + message + "\"}"
        );
        liveDataHandler.broadcast(
                "{ \"type\":\"STRATEGY_PNL\", " +
                        "\"strategy\":\"" + signal.getStrategyName() + "\"," +
                        "\"pnl\":" + strategyPnL.get(signal.getStrategyName()) +
                        "}"
        );
        // 🔥 Update dashboard
        broadcastPnL();
        broadcastRisk();
    }

    public void evaluateExit(String symbol, double price) throws Exception {

        Map<String, Position> strategyPositions = positionMap.get(symbol);

        if (strategyPositions == null) return;

        for (Map.Entry<String, Position> entry : strategyPositions.entrySet()) {

            String strategyName = entry.getKey();   // ✅ NOW YOU HAVE IT
            Position position = entry.getValue();
            {

                if (!position.isOpen()) continue;

                // 🔥 Update highest price
                if (price > position.getHighestPrice()) {
                    position.setHighestPrice(price);
                }

                double entryPrice = position.getEntryPrice();
                double stopLoss = position.getStopLoss();
                int qty = position.getQuantity();

                // 🔥 1. STOP LOSS HIT
                if (price <= stopLoss) {

                    double pnl = (price - entryPrice) * qty;
                    totalPnL += pnl;

                    TradeSignal exitSignal = new TradeSignal(
                            symbol,
                            "SELL",
                            price,
                            strategyName,
                            0
                    );

                    processSignal(exitSignal);
                    String message = "❌ SL HIT: " + symbol +
                            " Exit=" + price +
                            " PnL=" + pnl;
                    System.out.println(message);
                    // 🔥 ADD THIS (MISSING)
                    try {
                        liveDataHandler.broadcast(
                                "{ \"type\":\"LOG\", \"message\":\"" + message + "\"}"
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    broadcastPnL();
                    return;
                }

                // 🔥 2. PARTIAL PROFIT BOOKING
                double risk = entryPrice - stopLoss;
                double target1 = entryPrice + risk;

                if (!position.isPartialBooked() && price >= target1) {

                    int halfQty = qty / 2;
                    TradeSignal partialExit = new TradeSignal(
                            symbol,
                            "SELL",
                            price,
                            strategyName,
                            stopLoss
                    );
                    processSignal(partialExit);
                    double pnl = (price - entryPrice) * halfQty;
                    totalPnL += pnl;

                    position.setQuantity(qty - halfQty);
                    position.setPartialBooked(true);
                    String message = "✅ PARTIAL EXIT: " + symbol +
                            " Qty=" + halfQty +
                            " PnL=" + pnl;
                    System.out.println(message);
                    liveDataHandler.broadcast(
                            "{ \"type\":\"LOG\", \"message\":\"" + message + "\"}"
                    );
                    broadcastPnL();
                }

                // 🔥 3. TRAILING STOP LOSS
                double newSL = price - (risk * 0.5);

                if (newSL > stopLoss) {
                    position.setStopLoss(newSL);
                }
            }
        }
    }

    private void updateTrailingStop(Position position, double currentPrice) {

        // 🔥 If new high → update trailing SL
        if (currentPrice > position.getHighestPrice()) {

            position.setHighestPrice(currentPrice);

            // Example: trail by 1%
            double newSL = currentPrice * 0.99;

            // Only move SL upwards (never down)
            if (newSL > position.getStopLoss()) {
                position.setStopLoss(newSL);

                System.out.println("🔼 Trailing SL updated to: " + newSL);
            }
        }
    }

    private void updateDrawdown() {

        if (totalPnL > peakPnL) {
            peakPnL = totalPnL;
        }

        double drawdown = peakPnL - totalPnL;

        if (drawdown > maxDrawdown) {
            maxDrawdown = drawdown;
        }
    }

    public int getOpenPositionCount() {
        int count = 0;
        for (Map<String, Position> strategyPositions : positionMap.values()) {
            for (Position position : strategyPositions.values()) {
                if (position.isOpen()) count++;
            }
        }
        return count;
    }


    private void broadcastRisk() {
        try {
            double currentRisk = portfolioRiskManager.calculateCurrentRisk(positionMap);

            String status = "SAFE";

            if (currentRisk > 0.7 * 50000) status = "WARNING";
            if (currentRisk > 0.9 * 50000) status = "DANGER";

            liveDataHandler.broadcast(
                    "{ \"type\":\"RISK\", " +
                            "\"current\":" + currentRisk + "," +
                            "\"max\":50000," +
                            "\"pnl\":" + totalPnL + "," +
                            "\"drawdown\":" + maxDrawdown + "," +
                            "\"positions\":" + getOpenPositionCount() + "," +
                            "\"status\":\"" + status + "\"}"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastPnL() {
        try {
            liveDataHandler.broadcast(
                    "{ \"type\":\"PNL\", \"value\":" + totalPnL + "}"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}