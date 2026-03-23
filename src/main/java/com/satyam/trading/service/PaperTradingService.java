package com.satyam.trading.service;

import com.satyam.trading.model.Trade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaperTradingService {

    private final List<Trade> trades = new ArrayList<>();
    private double pnl = 0;

    // 🔥 BUY
    public void buy(String symbol, double price, int qty, String strategy) {

        Trade trade = new Trade(symbol, price, 0, qty, "BUY", true, strategy);
        trades.add(trade);

        System.out.println("🟢 PAPER BUY: " + symbol + " @ " + price + " Qty=" + qty);
    }

    // 🔥 SELL (UPDATED)
    public void sell(String symbol, double price, int qty, String strategy) {

        for (Trade trade : trades) {

            // match correct trade
            if (trade.isOpen() && trade.getSymbol().equals(symbol) && trade.getStrategy().equals(strategy)) {

                int availableQty = trade.getQuantity();

                // ❗ Safety
                if (availableQty <= 0) continue;

                int sellQty = Math.min(qty, availableQty);

                double profit = (price - trade.getEntryPrice()) * sellQty;
                pnl += profit;

                // 🔥 Reduce quantity (partial booking)
                trade.setQuantity(availableQty - sellQty);

                System.out.println("🔴 PAPER SELL: " + symbol +
                        " @ " + price +
                        " Qty=" + sellQty +
                        " PnL=" + profit);

                // 🔥 Close trade if fully exited
                if (trade.getQuantity() == 0) {
                    trade.setExitPrice(price);
                    trade.setOpen(false);
                }

                return;
            }
        }

        System.out.println("⚠️ No matching trade found for SELL: " + symbol);
    }

    public double getPnl() {
        return pnl;
    }
}