package com.satyam.trading.authentication;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${kite.api.key}")
    private String apiKey;

    @Value("${kite.api.secret}")
    private String apiSecret;

    public KiteConnect getConnection(String accessToken) {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setAccessToken(accessToken);
        return kiteConnect;
    }

    public String generateLoginUrl() {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        return kiteConnect.getLoginURL();
    }

    public String generateAccessToken(String requestToken) throws Exception, KiteException {
        KiteConnect kiteConnect = new KiteConnect(apiKey);

        User user = kiteConnect.generateSession(requestToken, apiSecret);

        return user.accessToken;
    }
}