package com.satyam.trading.strategy;

import java.util.HashMap;
import java.util.Map;

public class InstrumentRegistry {

    public static final Map<Long, String> tokenToSymbol = new HashMap<>();

    static {
        tokenToSymbol.put(738561L, "RELIANCE");
        tokenToSymbol.put(256265L, "NIFTY50");
        tokenToSymbol.put(779521L, "SBIN");
        tokenToSymbol.put(2763265L, "CANBK");
        tokenToSymbol.put(2752769L, "UNIONBANK");
        tokenToSymbol.put(877057L, "TATAPOWER");
        tokenToSymbol.put(895745L, "TATASTEEL");
        tokenToSymbol.put(3663105L, "INDIANB");
        tokenToSymbol.put(364545L, "HINDZINC");
        tokenToSymbol.put(2615553L, "ADANIENSOL");
        tokenToSymbol.put(912129L, "ADANIGREEN");
        tokenToSymbol.put(3660545L, "PFC");
        tokenToSymbol.put(5215745L, "COALINDIA");
        tokenToSymbol.put(3001089L, "JSWSTEEL");
        tokenToSymbol.put(779521L, "ADANIPOWER");


    }
}