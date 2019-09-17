package net.btcmarkets.sample.client.java;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppLauncher {
    
    public static void main(String[] args) {
        SpringApplication.run(AppLauncher.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(TradingService service) {
        return args -> {
            service.getAllOrders();
            //service.placeOrder();
        };
    }

}

