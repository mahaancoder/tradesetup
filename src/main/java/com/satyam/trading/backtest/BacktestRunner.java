package com.satyam.trading.backtest;

import com.satyam.trading.model.SampleData;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BacktestRunner implements CommandLineRunner {

    private final BacktestEngine backtestEngine;

    @Override
    public void run(String... args) throws Exception {

//        backtestEngine.runBacktest( SampleData.getData(), "RELIANCE");
    }
}