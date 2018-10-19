package com.kerby.example.packages.service;

import com.kerby.example.packages.exceptions.PackageNotFoundException;
import com.kerby.example.packages.exceptions.PackageServiceException;
import com.kerby.example.packages.models.Currency;
import com.kerby.example.packages.models.Package;
import com.kerby.example.packages.models.Product;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface PackageService {

    List<Currency> getCurrencies();

    int createPackage(@NotNull String name, String description, @NotNull Product... products) throws PackageServiceException;

    Package getPackage(String exchangeToCurrencyCode, @NotNull int packageId) throws PackageNotFoundException, PackageServiceException;

    List<Package> getPackages(String exchangeToCurrencyCode) throws PackageServiceException;

    boolean updatePackage(@NotNull int packageId, String name, String description, Product... products) throws PackageNotFoundException, PackageServiceException;

    boolean deletePackage(@NotNull int packageId) throws PackageNotFoundException, PackageServiceException;

}
