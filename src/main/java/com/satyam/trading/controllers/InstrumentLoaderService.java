package com.satyam.trading.controllers;

import com.satyam.trading.marketConnect.MarketDataService;
import com.satyam.trading.strategy.InstrumentRegistry;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Instrument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service to load and populate instrument data into InstrumentRegistry
 */
@Service
@RequiredArgsConstructor
public class InstrumentLoaderService {

    private final MarketDataService marketDataService;
    List<String> largeCapStocks = List.of("RELIANCE",
            "TCS",
            "HDFCBANK",
            "ICICIBANK",
            "INFY",
            "HINDUNILVR",
            "ITC",
            "SBIN",
            "BHARTIARTL",
            "LT",
            "KOTAKBANK",
            "AXISBANK",
            "ASIANPAINT",
            "BAJFINANCE",
            "MARUTI",
            "SUNPHARMA",
            "TITAN",
            "ULTRACEMCO",
            "WIPRO",
            "NESTLEIND",
            "POWERGRID",
            "NTPC",
            "HCLTECH",
            "TECHM",
            "ADANIENT",
            "ADANIPORTS",
            "COALINDIA",
            "ONGC",
            "TATASTEEL",
            "JSWSTEEL",
            "GRASIM",
            "SHREECEM",
            "DRREDDY",
            "DIVISLAB",
            "CIPLA",
            "EICHERMOT",
            "HEROMOTOCO",
            "BAJAJ-AUTO",
            "BAJAJFINSV",
            "INDUSINDBK",
            "HDFCLIFE",
            "SBILIFE",
            "ICICIPRULI",
            "BRITANNIA",
            "DABUR",
            "PIDILITIND",
            "GODREJCP",
            "COLPAL",
            "HAVELLS",
            "VOLTAS",
            "BERGEPAINT",
            "TATAMOTORS",
            "M&M",
            "APOLLOHOSP",
            "MAXHEALTH",
            "TORNTPHARM",
            "ALKEM",
            "BIOCON",
            "LUPIN",
            "AUROPHARMA",
            "ABB",
            "SIEMENS",
            "BOSCHLTD",
            "CUMMINSIND",
            "HINDALCO",
            "VEDL",
            "NMDC",
            "SAIL",
            "AMBUJACEM",
            "ACC",
            "RAMCOCEM",
            "DLF",
            "LODHA",
            "OBEROIRLTY",
            "GODREJPROP",
            "INDIGO",
            "IRCTC",
            "ZOMATO",
            "NYKAA",
            "PAYTM",
            "POLYCAB",
            "KEI",
            "SRF",
            "DEEPAKNTR",
            "PIIND",
            "AARTIIND",
            "UPL",
            "CHOLAFIN",
            "MUTHOOTFIN",
            "BAJAJHLDNG",
            "RECLTD",
            "PFC",
            "LICI");

    List<String> midCapStocks = List.of("AARTIIND",
            "ABBOTINDIA",
            "ABCAPITAL",
            "ABFRL",
            "ACC",
            "ADANIGREEN",
            "ADANIPOWER",
            "AIAENG",
            "AJANTPHARM",
            "ALKEM",
            "AMBUJACEM",
            "APLLTD",
            "APOLLOTYRE",
            "ASHOKLEY",
            "ASTRAL",
            "ATUL",
            "AUBANK",
            "AUROPHARMA",
            "BALKRISIND",
            "BANDHANBNK",
            "BATAINDIA",
            "BHARATFORG",
            "BIOCON",
            "BLUEDART",
            "CANFINHOME",
            "CHAMBLFERT",
            "COFORGE",
            "CONCOR",
            "CROMPTON",
            "CUMMINSIND",
            "DALBHARAT",
            "DEEPAKNTR",
            "DIVISLAB",
            "DIXON",
            "ESCORTS",
            "EXIDEIND",
            "FEDERALBNK",
            "FORTIS",
            "GAIL",
            "GLAND",
            "GMRINFRA",
            "GODREJIND",
            "GRANULES",
            "GUJGASLTD",
            "HAL",
            "HATSUN",
            "HINDPETRO",
            "IDFCFIRSTB",
            "INDHOTEL",
            "INDIAMART",
            "INDUSTOWER",
            "IPCALAB",
            "JBCHEPHARM",
            "JINDALSTEL",
            "JSWENERGY",
            "JUBLFOOD",
            "KAJARIACER",
            "KALPATPOWR",
            "KANSAINER",
            "KEC",
            "KIRLOSENG",
            "LALPATHLAB",
            "LAURUSLABS",
            "LICHSGFIN",
            "LTIM",
            "LTTS",
            "MANAPPURAM",
            "MARICO",
            "MCX",
            "METROPOLIS",
            "MFSL",
            "MGL",
            "MINDTREE",
            "MOTILALOFS",
            "MPHASIS",
            "MRPL",
            "MUTHOOTFIN",
            "NATCOPHARM",
            "NAVINFLUOR",
            "NMDC",
            "OBEROIRLTY",
            "OFSS",
            "OIL",
            "PAGEIND",
            "PERSISTENT",
            "PETRONET",
            "PFIZER",
            "POLYCAB",
            "POWERINDIA",
            "PRESTIGE",
            "RAIN",
            "RAMCOCEM",
            "RECLTD",
            "SAIL",
            "SANOFI",
            "SCHAEFFLER",
            "SRF",
            "STAR",
            "SUNTV",
            "SYNGENE",
            "TATACHEM",
            "TATACOMM",
            "TATAELXSI",
            "TORNTPHARM",
            "TORNTPOWER",
            "TVSMOTOR",
            "UBL",
            "UNIONBANK",
            "VGUARD",
            "VOLTAS",
            "WHIRLPOOL",
            "ZENSARTECH",
            "ZEEL",
            "ZFCVINDIA",
            "ZYDUSLIFE"
    );


    /**
     * Load all NSE EQ instruments and populate the tokenToSymbol map
     * Filters instruments with instrument_type = "EQ" (Equity)
     * 
     * @param accessToken User's access token
     * @return Number of instruments loaded
     */
    public Map<Long, String> loadNSEEquityInstruments(String accessToken) throws Exception, KiteException {
        System.out.println("🔄 Loading NSE Equity instruments...");
        
        // Get all NSE instruments
        List<Instrument> instruments = marketDataService.getInstrumentsForExchange(accessToken, "NSE");
        InstrumentRegistry.tokenToSymbol.clear();
        int count = 0;
        
        // Filter and populate only EQ (Equity) instruments
        for (Instrument instrument : instruments) {
            // Check if instrument_type is "EQ"
            if ("EQ".equals(instrument.getInstrument_type())) {
                long token = instrument.getInstrument_token();
                String symbol = instrument.getTradingsymbol();
                if(symbol.matches(".*\\d.*")) continue;
                // Add to the map
                if(largeCapStocks.contains(symbol) || midCapStocks.contains(symbol)) {
                    InstrumentRegistry.tokenToSymbol.put(token, symbol);
                    count++;
                }
            }
        }

        System.out.println("✅ Loaded " + count + " NSE Equity instruments into InstrumentRegistry");
        return InstrumentRegistry.tokenToSymbol;
    }

    /**
     * Load instruments from multiple exchanges
     * 
     * @param accessToken User's access token
     * @param exchanges Array of exchange names (e.g., "NSE", "BSE")
     * @return Total number of instruments loaded
     */
    public int loadEquityInstruments(String accessToken, String... exchanges) throws Exception, KiteException {
        int totalCount = 0;
        
        for (String exchange : exchanges) {
            System.out.println("🔄 Loading " + exchange + " Equity instruments...");
            
            List<Instrument> instruments = marketDataService.getInstrumentsForExchange(accessToken, exchange);
            
            int count = 0;
            for (Instrument instrument : instruments) {
                if ("EQ".equals(instrument.getInstrument_type())) {
                    long token = instrument.getInstrument_token();
                    String symbol = instrument.getTradingsymbol();
                    if (!symbol.matches(".*\\d.*")) {
                        InstrumentRegistry.tokenToSymbol.put(token, symbol);
                        count++;
                    }
                }
            }
            
            System.out.println("✅ Loaded " + count + " " + exchange + " Equity instruments");
            totalCount += count;
        }
        
        System.out.println("✅ Total instruments loaded: " + totalCount);
        return totalCount;
    }

    /**
     * Clear all instruments from the registry
     */
    public void clearInstruments() {
        InstrumentRegistry.tokenToSymbol.clear();
        System.out.println("🗑️  Cleared all instruments from InstrumentRegistry");
    }

    /**
     * Get the current count of instruments in the registry
     */
    public int getInstrumentCount() {
        return InstrumentRegistry.tokenToSymbol.size();
    }
}

