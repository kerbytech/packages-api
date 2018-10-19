package com.kerby.example.currency;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.kerby.example.currency")
@PropertySource("classpath:config/fixer-service.properties")
public class CurrencyServiceConfig {
}
