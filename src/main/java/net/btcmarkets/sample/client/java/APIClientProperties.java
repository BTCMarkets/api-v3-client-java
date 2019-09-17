package net.btcmarkets.sample.client.java;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("api")
public class APIClientProperties {
    private String baseUrl;
    private String key;
    private String secret;
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
}
