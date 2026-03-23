package com.satyam.trading.service;

import com.satyam.trading.model.StrategyParams;

import java.util.ArrayList;
import java.util.List;

public class ParameterGenerator {

    public static List<StrategyParams> generate() {

        List<StrategyParams> list = new ArrayList<>();

        double[] buffers = {0.001, 0.002, 0.003};
        double[] atrs = {1.2, 1.5, 2.0};
        int[] emas = {20, 30};
        double[] pullbacks = {0.003, 0.005};

        for (double b : buffers) {
            for (double a : atrs) {
                for (int e : emas) {
                    for (double p : pullbacks) {

                        list.add(new StrategyParams(b, a, e, p));
                    }
                }
            }
        }

        return list;
    }
}