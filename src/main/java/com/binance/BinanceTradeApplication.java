package com.binance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.binance"})
public class BinanceTradeApplication {
	private static final Logger LOG = LoggerFactory.getLogger(BinanceTradeApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(BinanceTradeApplication.class, args);
		LOG.info("[===binance-trade-server starting==]");
		
	}
}
