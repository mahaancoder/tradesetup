package com.satyam.trading.model;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    public static List<Candle> getData() {

        List<Candle> candles = new ArrayList<>();

        candles.add(new Candle(100, 102, 99, 101,   100, null));
        candles.add(new Candle(101, 103, 100, 102, 200, null));
        candles.add(new Candle(102, 104, 101, 103, 300, null));
        candles.add(new Candle(103, 105, 102, 104, 200, null));
        candles.add(new Candle(104, 106, 103, 105, 150, null));

        return candles;
    }
}