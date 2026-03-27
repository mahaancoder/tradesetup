package com.satyam.trading.authentication;

import com.satyam.trading.controllers.InstrumentLoaderService;
import com.satyam.trading.executionengine.ExecutionEngine;
import com.satyam.trading.websocket.WebSocketService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/kite")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenStore tokenStore;
    private final ExecutionEngine executionEngine;
    private final InstrumentLoaderService instrumentLoaderService;
    private final WebSocketService webSocketService;
    private boolean isStarted = false;
    @Value("${kite.api.key}")
    private String apiKey;

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws Exception {
        String url = authService.generateLoginUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("request_token") String requestToken,
                         HttpServletResponse response) throws Exception, KiteException {

        String accessToken = authService.generateAccessToken(requestToken);

        // ✅ Save token
        tokenStore.setAccessToken(accessToken);
        instrumentLoaderService.loadNSEEquityInstruments(accessToken);
        if(!isStarted) {
            webSocketService.startTicker(apiKey, accessToken);
            isStarted = true;
        }
        // ✅ Redirect back to UI
        response.sendRedirect("/?status=success");
    }

    @GetMapping("/status")
    public String status() {
        return tokenStore.getAccessToken() != null ? "LOGGED_IN" : "NOT_LOGGED_IN";
    }

    @GetMapping("/api/pnl")
    public Map<String, Double> pnl() {
        return Map.of("pnl", executionEngine.getTotalPnL());
    }

}