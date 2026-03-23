package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Candle {
        private double open;
        private double high;
        private double low;
        private double close;
        private long volume;
        private LocalDateTime time;
}