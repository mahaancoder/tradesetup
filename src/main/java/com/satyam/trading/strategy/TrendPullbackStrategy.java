package com.satyam.trading.strategy;

import com.satyam.trading.model.TradeSignal;
import com.satyam.trading.service.VolumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrendPullbackStrategy implements TradingStrategy {

    private final EMAService emaService;
    private final VolumeService volumeService;
    private final MarketTrendService marketTrendService;
    private final GapFilterService gapFilterService;

    private final Map<String, Boolean> positionTaken = new HashMap<>();

    private static final LocalTime START = LocalTime.of(10, 30);
    private static final LocalTime END = LocalTime.of(14, 30);

    @Override
    public String getName() {
        return "TrendPullback";
    }

    @Override
    public TradeSignal onTick(String symbol, double price) {

        LocalTime now = LocalTime.now();

        if (now.isBefore(START) || now.isAfter(END)) {
            return null;
        }

        double ema = emaService.calculateEMA(symbol, price);

        boolean alreadyInTrade = positionTaken.getOrDefault(symbol, false);

        // 🔥 Trend condition
        if (price > ema && !alreadyInTrade
                && volumeService.isHighVolume(symbol)
                && marketTrendService.isBullish()
                && gapFilterService.isValidGap(symbol)) {

            double distance = (price - ema) / ema;

            // 🔥 Pullback condition (within 0.5%)
            if (distance < 0.005) {

                positionTaken.put(symbol, true);

                double stopLoss = ema * 0.995;

                return new TradeSignal(
                        symbol,
                        "BUY",
                        price,
                        getName(),
                        stopLoss
                );
            }
        }

        return null;
    }
}