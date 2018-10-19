package com.kerby.example.database;

import com.kerby.example.database.repositories.PackageRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackageClasses = PackageRepository.class) // auto configure embedded H2 repos
@EntityScan("com.kerby.example.database.models")
public class DatabaseConfig {
}
