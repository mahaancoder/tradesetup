package com.satyam.trading.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveDataHandler liveDataHandler;

    public WebSocketConfig(LiveDataHandler liveDataHandler) {
        this.liveDataHandler = liveDataHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(liveDataHandler, "/ws/live")
                .setAllowedOrigins("*");
    }
}