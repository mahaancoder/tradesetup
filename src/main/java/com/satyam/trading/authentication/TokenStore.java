package com.satyam.trading.authentication;

import org.springframework.stereotype.Component;

@Component
public class TokenStore {

    private String accessToken="X7hKVMF2fQfrF9pN0XZFQqBFYj8g9lh5";

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        System.out.println("✅ Access Token Stored: " + accessToken);
    }
}