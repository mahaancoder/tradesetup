package com.satyam.trading.websocket;

import com.satyam.trading.executionengine.ExecutionEngine;
import com.satyam.trading.service.*;
import com.satyam.trading.strategy.GapFilterService;
import com.satyam.trading.strategy.InstrumentRegistry;
import com.satyam.trading.strategy.MarketTrendService;
import com.satyam.trading.strategy.StrategyManager;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final StrategyManager strategyManager;
    private final ExecutionEngine executionEngine;
    private final LiveDataHandler liveDataHandler;
    private final StockSelectionService stockSelectionService;
    private final VolumeService volumeService;
    private final GapFilterService gapFilterService;
    private final MarketTrendService marketTrendService;

    public void startTicker(String apiKey, String accessToken) {

        KiteTicker ticker = new KiteTicker(accessToken, apiKey);

        ticker.setOnConnectedListener(() -> {
            System.out.println("Connected");

            ArrayList<Long> tokens = new ArrayList<>(InstrumentRegistry.tokenToSymbol.keySet());

            ticker.subscribe(tokens);
            ticker.setMode(tokens, KiteTicker.modeFull);
        });

        ticker.setOnTickerArrivalListener(ticks -> {

            // 🔥 STEP 1: FIRST update all data
            for (Tick tick : ticks) {

                String symbol = InstrumentRegistry.tokenToSymbol.get(tick.getInstrumentToken());
                if (symbol == null) continue;

                double price = tick.getLastTradedPrice();

                // update open price (only once internally)
                gapFilterService.setTodayOpen(symbol, price);

                // update price for selection
                stockSelectionService.updatePrice(symbol, price);

                // update volume
                double volume = tick.getVolumeTradedToday();
                volumeService.updateVolume(symbol, volume > 0 ? volume : 1);

                // update market trend
                if ("NIFTY 50".equals(symbol)) {
                    marketTrendService.updatePrice(price);
                }

                // update execution price
                executionEngine.updatePrice(symbol, price);
            }

            // 🔥 STEP 2: NOW compute top movers (after data update)
            List<String> selectedStocks = stockSelectionService.getTopMovers();

            Set<String> selectedSet = new HashSet<>(selectedStocks);

            // 🔥 STEP 3: skip if not enough data (warm-up)
            if (selectedStocks.isEmpty()) {
                return;
            }

            // 🔥 STEP 4: PROCESS only selected stocks
            for (Tick tick : ticks) {

                String symbol = InstrumentRegistry.tokenToSymbol.get(tick.getInstrumentToken());
                if (symbol == null || !selectedSet.contains(symbol)) continue;

                double price = tick.getLastTradedPrice();

                try {
                    // ✅ exit first
                    executionEngine.evaluateExit(symbol, price);

                    // ✅ then strategy
                    strategyManager.processTick(symbol, price);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 🔥 UI broadcast
                try {
                    liveDataHandler.broadcast(
                            "{ \"type\":\"TICK\", \"symbol\":\"" + symbol + "\", \"price\":" + price + "}"
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ticker.connect();
    }
}