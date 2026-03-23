package com.satyam.trading.marketConnect;

import com.satyam.trading.authentication.AuthService;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final AuthService authService;

    public double getLtp(String accessToken, String instrument) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);

        Map<String, LTPQuote> ltp = kite.getLTP(new String[]{instrument});

        return ltp.get(instrument).lastPrice;
    }

    public List<Instrument> getInstruments(String accessToken) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);
        return kite.getInstruments();
    }

    public List<Instrument> getInstrumentsForExchange(String accessToken, String exchange) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);
        return kite.getInstruments(exchange);
    }

    public Map<String, Quote> getQuote(String accessToken, String [] instruments) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);
        return kite.getQuote(instruments);
    }

    public Map<String, OHLCQuote> getOHLC(String accessToken, String [] instruments) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);
        return kite.getOHLC(instruments);
    }

    public Map<String, TriggerRange> getTriggerRange(String accessToken,String[] instruments, String transactionType) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);
        return kite.getTriggerRange(instruments, transactionType);
    }

    public HistoricalData getHistoricalData(String accessToken, Date from, Date to, String token, String interval, boolean continuous, boolean oi) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);
        return kite.getHistoricalData(from, to, token, interval, continuous, oi);
    }

}