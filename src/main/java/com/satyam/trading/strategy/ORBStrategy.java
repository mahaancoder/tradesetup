package com.satyam.trading.strategy;

import com.satyam.trading.model.TradeSignal;
import com.satyam.trading.service.ATRService;
import com.satyam.trading.service.VolumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ORBStrategy implements TradingStrategy {
    private final VolumeService volumeService;
    private final MarketTrendService marketTrendService;
    private final GapFilterService gapFilterService;
    private final ATRService atrService;
    // 🔥 Per-symbol state
    private final Map<String, Double> highMap = new HashMap<>();
    private final Map<String, Double> lowMap = new HashMap<>();
    private final Map<String, Boolean> rangeCapturedMap = new HashMap<>();
    private final Map<String, Boolean> tradeTakenMap = new HashMap<>();
    private final Map<String, Long> lastTradeTimeMap = new HashMap<>();

    // 🔥 Config
    private static final LocalTime RANGE_START = LocalTime.of(9, 35);
    private static final LocalTime RANGE_END = LocalTime.of(9, 40);
    private static final LocalTime TRADE_CUTOFF = LocalTime.of(11, 0);

    private static final double BREAKOUT_BUFFER = 0.001; // 0.1%
    private static final long COOLDOWN_MILLIS = 5 * 60 * 1000; // 5 min


    @Override
    public String getName() {
        return "ORBStrategy";
    }

    @Override
    public TradeSignal onTick(String symbol, double price) {

        LocalTime now = LocalTime.now();

//        // ❌ Do not trade after cutoff time
//        if (now.isAfter(TRADE_CUTOFF)) {
//            return null;
//        }

        // 🔥 Get current state
        double high = highMap.getOrDefault(symbol, Double.MIN_VALUE);
        double low = lowMap.getOrDefault(symbol, Double.MAX_VALUE);

        boolean rangeCaptured = rangeCapturedMap.getOrDefault(symbol, false);
        boolean tradeTaken = tradeTakenMap.getOrDefault(symbol, false);

        // 🔥 Step 1: Capture Opening Range
        if (!rangeCaptured) {

            if (!now.isBefore(RANGE_START) && now.isBefore(RANGE_END)) {

                high = Math.max(high, price);
                low = Math.min(low, price);

                highMap.put(symbol, high);
                lowMap.put(symbol, low);
                System.out.println(symbol+" high: " + high);
                System.out.println(symbol+" low: " + low);
                return null;
            }

            // Lock range after 9:30
            if (now.isAfter(RANGE_END)) {
                rangeCapturedMap.put(symbol, true);
                System.out.println("📊 ORB Range Set [" + symbol + "] High=" + high + " Low=" + low);
            }
        }

        // ❌ Avoid multiple trades per day
        if (tradeTaken) {
            return null;
        }

        // 🔥 Cooldown check
        if (lastTradeTimeMap.containsKey(symbol)) {
            long lastTradeTime = lastTradeTimeMap.get(symbol);

            if (System.currentTimeMillis() - lastTradeTime < COOLDOWN_MILLIS) {
                return null;
            }
        }

        // 🔥 Breakout logic with buffer
        double breakoutLevel = high * (1 + BREAKOUT_BUFFER);

        if (price > breakoutLevel
                && volumeService.isHighVolume(symbol)
                && marketTrendService.isBullish()
                && gapFilterService.isValidGap(symbol)) {

            // ✅ Mark trade taken
            tradeTakenMap.put(symbol, true);
            lastTradeTimeMap.put(symbol, System.currentTimeMillis());
            // 🔥 SL = range low
            double atr = atrService.calculateATR(symbol, high, low, price);
            System.out.println("symbol: "+ symbol + " high : "+ high + " low : "+ low + " atr : "+ atr);
            double stopLoss = price - (atr * 1.5); // multiplier
            System.out.println("🚀 ORB BUY Triggered [" + symbol + "] @ " + price + "with stoploss @ "+ stopLoss);
            return new TradeSignal(
                    symbol,
                    "BUY",
                    price,
                    getName(),
                    stopLoss
            );
        }

        return null;
    }
}