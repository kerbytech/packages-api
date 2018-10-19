package com.kerby.example.packages;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.kerby.example.packages")
public class PackageConfig {
}
