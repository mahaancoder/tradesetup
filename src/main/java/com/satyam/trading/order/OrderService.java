package com.satyam.trading.order;

import com.satyam.trading.authentication.AuthService;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AuthService authService;

    public Order placeBuyOrder(String accessToken, String symbol, int qty) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);

        OrderParams params = new OrderParams();
        params.exchange = "NSE";
        params.tradingsymbol = symbol;
        params.transactionType = "BUY";
        params.quantity = qty;
        params.orderType = "MARKET";
        params.product = "MIS";

        return kite.placeOrder(params, "regular");
    }

    public Order placeSellOrder(String accessToken, String symbol, int qty) throws Exception, KiteException {
        KiteConnect kite = authService.getConnection(accessToken);

        OrderParams params = new OrderParams();
        params.exchange = "NSE";
        params.tradingsymbol = symbol;
        params.transactionType = "SELL";
        params.quantity = qty;
        params.orderType = "MARKET";
        params.product = "MIS";

        return kite.placeOrder(params, "regular");
    }
}