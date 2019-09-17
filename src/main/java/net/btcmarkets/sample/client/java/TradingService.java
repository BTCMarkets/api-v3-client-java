package net.btcmarkets.sample.client.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

@Service
public class TradingService {
    private static final Logger LOG = LoggerFactory.getLogger(TradingService.class);
    private static final String ORDERS_PATH = "/v3/orders";
    private static final String ORDERS_STATUS_QUERY = "status=all";
    
    private final APIClient client;

    public TradingService(APIClient client) {
        this.client = client;
    }
    
    public void placeOrder() {
        String postData = "{\"marketId\":\"XRP-AUD\",\"price\":\"0.23\",\"amount\":\"1.0\",\"side\":\"Bid\",\"type\":\"Limit\"}";
        try {
            ResponseEntity<String> result = client.callHttpPost(ORDERS_PATH, postData);
            LOG.info("placed order successfully ", result.getBody());
            
        } catch (HttpStatusCodeException e) {
            String responseBody = e.getResponseBodyAsString();
            LOG.error("placing order returned http error {} and detail error message {} ", e.getStatusCode(), responseBody);
        } catch (RestClientException e) {
            LOG.error("error placing order ", e);
        }
    }
    
    // getting orders for all markets and all statues
    public void getAllOrders() {
        try {
            ResponseEntity<String> result = client.callHttpGet(ORDERS_PATH, ORDERS_STATUS_QUERY); 
            LOG.info(result.getBody());
        } catch (HttpStatusCodeException e) {
            String responseBody = e.getResponseBodyAsString();
            LOG.error("getting orders returned http error {} and detail error message {} ", e.getStatusCode(), responseBody);
        } catch (RestClientException e) {
            LOG.error("error getting orders ", e);
        }
    }

}
