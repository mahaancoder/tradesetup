package com.satyam.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class Split {
    private List<Candle> train;
    private List<Candle> test;
}