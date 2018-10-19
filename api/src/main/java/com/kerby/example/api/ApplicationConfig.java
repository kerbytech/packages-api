package com.kerby.example.api;

import com.kerby.example.currency.CurrencyServiceConfig;
import com.kerby.example.database.DatabaseConfig;
import com.kerby.example.packages.PackageConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {PackageConfig.class, DatabaseConfig.class, CurrencyServiceConfig.class})
public class ApplicationConfig {
}
