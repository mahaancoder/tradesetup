package com.satyam.trading.websocket;

import com.satyam.trading.authentication.TokenStore;
import com.satyam.trading.controllers.InstrumentLoaderService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketRunner implements CommandLineRunner {

    private final WebSocketService webSocketService;
    private final TokenStore tokenStore;
    private final InstrumentLoaderService instrumentLoaderService;
    @Value("${kite.api.key}")
    private String apiKey;

    @Override
    public void run(String... args) throws Exception {
        String accessToken = tokenStore.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            System.out.println("⚠️  No access token found. WebSocket connection not started.");
            System.out.println("💡 Please authenticate first by visiting: http://localhost:8080/kite/login");
            System.out.println("💡 Or set the access token manually using TokenStore.setAccessToken()");
            return;
        }

        System.out.println("🚀 Starting WebSocket connection with access token...");
        try {
            instrumentLoaderService.loadNSEEquityInstruments(accessToken);
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }
        webSocketService.startTicker(apiKey, accessToken);
    }
}