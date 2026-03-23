package com.satyam.trading.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VolumeService {

    private final Map<String, Double> avgVolumeMap = new HashMap<>();
    private final Map<String, Double> currentVolumeMap = new HashMap<>();

    public void updateVolume(String symbol, double volume) {

        currentVolumeMap.put(symbol,
                currentVolumeMap.getOrDefault(symbol, 0.0) + volume);

        avgVolumeMap.put(symbol,
                (avgVolumeMap.getOrDefault(symbol, 0.0) + volume) / 2);
    }

    public boolean isHighVolume(String symbol) {

        double current = currentVolumeMap.getOrDefault(symbol, 0.0);
        double avg = avgVolumeMap.getOrDefault(symbol, 1.0);

        return current > 1.5 * avg; // 🔥 threshold
    }
}