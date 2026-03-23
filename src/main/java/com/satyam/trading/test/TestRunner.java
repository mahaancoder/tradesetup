package com.satyam.trading.test;

import com.satyam.trading.marketConnect.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final MarketDataService marketDataService;
    private final String ACCESS_TOKEN = "cmbcya4fy9FF9ufsgesRXKwLioqe1uj6";

    @Override
    public void run(String... args) throws Exception {
        // Uncomment below to test various Kite APIs

//        try {
//            double price = marketDataService.getLtp(ACCESS_TOKEN, "NSE:RELIANCE");
//            System.out.println("App Started Successfully");
//            System.out.println("RELIANCE Price: " + price);
//            System.out.println("==============================");
//            System.out.println(marketDataService.getInstruments(ACCESS_TOKEN).get(10000));
//            System.out.println("==============================");
//        try {
//            System.out.println(marketDataService.getInstrumentsForExchange(ACCESS_TOKEN, "NSE"));
//        } catch (KiteException e) {
//            throw new RuntimeException(e);
//        }
//            System.out.println("==============================");
//            System.out.println(marketDataService.getQuote(ACCESS_TOKEN, new String[]{"NSE:RELIANCE"}));
//            System.out.println("==============================");
//            System.out.println(marketDataService.getOHLC(ACCESS_TOKEN, new String[]{"NSE:RELIANCE"}));
//            System.out.println("==============================");
//            System.out.println(marketDataService.getTriggerRange(ACCESS_TOKEN, new String[]{"NSE:RELIANCE"}, "BUY"));
//            System.out.println("==============================");
//
//            // Test historical data API (requires instrument token, not symbol)
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date fromDate = sdf.parse("2026-03-01");
//            Date toDate = sdf.parse("2026-03-19");
//            System.out.println(marketDataService.getHistoricalData(ACCESS_TOKEN, fromDate, toDate, "738561", "60minute", false, false));
//        } catch (KiteException e) {
//            throw new RuntimeException(e);
//        }

        System.out.println("✅ Application started successfully!");
        System.out.println("📡 Server running at: http://localhost:8080");
        System.out.println("🔐 Login URL: http://localhost:8080/kite/login");
    }
}