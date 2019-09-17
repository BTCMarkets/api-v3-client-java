package net.btcmarkets.sample.client.java;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@EnableConfigurationProperties(APIClientProperties.class)
public class APIClient {
    private static final Logger LOG = LoggerFactory.getLogger(APIClient.class);
    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM = "HmacSHA512";
    private static final String APIKEY_HEADER = "BM-AUTH-APIKEY";
    private static final String TIMESTAMP_HEADER = "BM-AUTH-TIMESTAMP";
    private static final String SIGNATURE_HEADER = "BM-AUTH-SIGNATURE";

    @Autowired
    private APIClientProperties prop;
    private RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    
    public ResponseEntity<String> callHttpPost(String path, String postData) throws RestClientException {

        String url = prop.getBaseUrl() + path;
        String now = Long.toString(System.currentTimeMillis());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, "application/json");
        headers.add(ACCEPT, "application/json");
        headers.add(ACCEPT_ENCODING, ENCODING);

        headers.add(APIKEY_HEADER, prop.getKey());
        headers.add(TIMESTAMP_HEADER, now);

        String stringToSign = buildStringToSign("POST", path, now, postData);
        String signature = signMessage(prop.getSecret(), stringToSign);

        headers.add(SIGNATURE_HEADER, signature);

        HttpEntity<String> data = new HttpEntity<String>(postData, headers);
        ResponseEntity<String> response = rest.exchange(url, POST, data, String.class);

        return response;

    }

    public ResponseEntity<String> callHttpGet(String path, String queryString) throws RestClientException {
        String fullPath = path;
        if (queryString != null) {
            fullPath += "?" + queryString;
        }
        String url = prop.getBaseUrl() + fullPath;
        String now = Long.toString(System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, "application/json");
        headers.add(ACCEPT, "application/json");
        headers.add(ACCEPT_ENCODING, ENCODING);

        headers.add(APIKEY_HEADER, prop.getKey());
        headers.add(TIMESTAMP_HEADER, now);

        String stringToSign = buildStringToSign("GET", path, now, null);
        String signature = signMessage(prop.getSecret(), stringToSign);

        headers.add(SIGNATURE_HEADER, signature);

        HttpEntity<String> data = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = rest.exchange(url, GET, data, String.class);

        return response;
    }

    private static String buildStringToSign(String method, String path, String timestamp, String postData) {
        String stringToSign = method + path + timestamp;
        if (postData != null) {
            stringToSign += postData;
        }
        return stringToSign;
    }
    
    private static String signMessage(String secret, String data) {
        String signature = "";
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            byte[] key = Base64.getDecoder().decode(secret);
            SecretKeySpec keSpec = new SecretKeySpec(key, ALGORITHM);
            mac.init(keSpec);
            signature = Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            LOG.error("unable to sign the request", e);
        }
        return signature;
    }
}
