package com.satyam.trading.strategy;

import com.satyam.trading.model.Candle;
import com.satyam.trading.model.Split;

import java.util.ArrayList;
import java.util.List;

public class WalkForwardSplitter {

    public static List<Split> split(List<Candle> candles, int trainSize, int testSize) {

        List<Split> splits = new ArrayList<>();

        for (int i = 0; i + trainSize + testSize <= candles.size(); i += testSize) {

            List<Candle> train = candles.subList(i, i + trainSize);
            List<Candle> test = candles.subList(i + trainSize, i + trainSize + testSize);

            splits.add(new Split(train, test));
        }

        return splits;
    }
}