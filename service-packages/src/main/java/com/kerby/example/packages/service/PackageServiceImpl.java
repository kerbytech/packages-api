package com.kerby.example.packages.service;

import com.kerby.example.currency.exceptions.InvalidCurrencyCodeException;
import com.kerby.example.currency.models.CurrencyCode;
import com.kerby.example.currency.service.CurrencyService;
import com.kerby.example.database.repositories.PackageRepository;
import com.kerby.example.database.models.PackageEntity;
import com.kerby.example.database.models.ProductEntity;
import com.kerby.example.packages.exceptions.PackageNotFoundException;
import com.kerby.example.packages.exceptions.PackageServiceException;
import com.kerby.example.packages.mapping.PackageMapper;
import com.kerby.example.packages.mapping.ProductMapper;
import com.kerby.example.packages.models.Currency;
import com.kerby.example.packages.models.Package;
import com.kerby.example.packages.models.Product;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageServiceImpl implements PackageService  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private CurrencyService currencyService;
    private PackageRepository packageRepository;

    public PackageServiceImpl(@Autowired CurrencyService currencyService, @Autowired PackageRepository packageRepository) {
        this.currencyService = currencyService;
        this.packageRepository = packageRepository;
    }

    @Override
    public List<Currency> getCurrencies() {
        return Arrays.stream(this.currencyService.getCurrencyCodes())
                .map(currencyCode -> new Currency(currencyCode.getCode(), currencyCode.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public int createPackage(@NotNull final String name, final String description, @NotNull final Product... products) throws PackageServiceException {
        Assert.isTrue(StringUtils.isNotBlank(name), "name is required to create a package");
        Assert.notNull(products, "One or more products are required to create a package");
        Assert.notEmpty(products, "One or more products are required to create a package");
        for (Product product : products) {
            Assert.isTrue(product.isValid(), "One or more products are invalid");
        }
        final int result;

        PackageEntity packageEntity = null;
        try {
            // create package
            logger.debug(String.format("Creating package with name: [%s] description: [%s] products: [%s]", name, description, products));
            final List<ProductEntity> productEntities = Arrays.stream(products)
                    .map(ProductMapper.INSTANCE::productToEntity)
                    .collect(Collectors.toList());
            packageEntity = new PackageEntity(name, description, productEntities);
            // save package
            result = this.packageRepository.save(packageEntity).getId();
            logger.info(String.format("Created package: [%d]", result));
        } catch (Exception e) {
            throw new PackageServiceException(String.format("Something went wrong saving package: [%s]", packageEntity), e);
        }

        return result;
    }

    @Override
    public Package getPackage(final String exchangeToCurrencyCode, @NotNull final int packageId) throws PackageNotFoundException, PackageServiceException {
        Assert.isTrue(packageId > 0, "packageId is required to create a package");
        CurrencyCode exchangeToCode = null;
        if (StringUtils.isNotBlank(exchangeToCurrencyCode)) {
            try {
                exchangeToCode = this.currencyService.getCurrencyCodeFromString(exchangeToCurrencyCode);
            } catch (InvalidCurrencyCodeException e) {
                throw new IllegalArgumentException("The currency code provided is invalid", e);
            }
        }
        final Package result;

        try {
            // find package
            logger.debug(String.format("Getting package with id: [%d", packageId));
            final PackageEntity packageEntity = this.packageRepository.findById(packageId)
                    .orElseThrow(() -> new PackageNotFoundException(String.format("No package found with id: [%d]", packageId)));
            result = PackageMapper.INSTANCE.entityToPackage(packageEntity);
            logger.debug(String.format("Found package: [%s]", result));
            if (exchangeToCode != null) {
                final BigDecimal usdTotalPrice = result.getPrice();
                logger.debug(String.format("Converting price: [%s] of package from USD to currency: [%s]", usdTotalPrice, exchangeToCode));
                result.setPrice(currencyService.convertFromUSD(exchangeToCode, usdTotalPrice));
            }
            logger.info(String.format("Returning package: [%s]", result));
        } catch (PackageNotFoundException e) {
           throw e;
        } catch (Exception e) {
            throw new PackageServiceException(String.format("Something went wrong looking up package: [%d]", packageId), e);
        }

        return result;
    }

    @Override
    public List<Package> getPackages(final String exchangeToCurrencyCode) throws PackageServiceException {
        CurrencyCode exchangeToCode = null;
        if (StringUtils.isNotBlank(exchangeToCurrencyCode)) {
            try {
                exchangeToCode = this.currencyService.getCurrencyCodeFromString(exchangeToCurrencyCode);
            } catch (InvalidCurrencyCodeException e) {
                throw new IllegalArgumentException("The currency code provided is invalid", e);
            }
        }
        final List<Package> result;

        try {
            // find packages
            logger.debug("Getting packages");
            final List<Package> packageEntities = new ArrayList<>();
            this.packageRepository.findAll().forEach(packageEntity ->
                packageEntities.add(PackageMapper.INSTANCE.entityToPackage(packageEntity))
            );
            result = packageEntities;
            logger.debug(String.format("Found packages: [%d]", result.size()));
            if (exchangeToCode != null) {
                for (Package packageEntity : packageEntities) {
                    final BigDecimal usdTotalPrice = packageEntity.getPrice();
                    logger.debug(String.format("Converting price: [%s] of package from USD to currency: [%s]", usdTotalPrice, exchangeToCode));
                    packageEntity.setPrice(currencyService.convertFromUSD(exchangeToCode, usdTotalPrice));
                }
            }
            logger.info(String.format("Returning packages: [%s]", result));
        } catch (Exception e) {
            throw new PackageServiceException("Something went wrong looking up packages", e);
        }

        return result;
    }

    @Override
    public boolean updatePackage(@NotNull final int packageId, String name, final String description, final Product... products) throws PackageNotFoundException, PackageServiceException {
        Assert.isTrue(packageId > 0, "packageId is required for update");
        boolean result = false;

        try {
            // find packages
            logger.debug(String.format("Getting package with id: [%d] to update", packageId));
            final Package aPackage = this.getPackage(null, packageId);
            logger.debug(String.format("Found package: [%s] to update", aPackage));
            if (aPackage != null) {
                // find packages
                logger.debug(String.format("Updating package id: [%d] with name: [%s] description: [%s] products: [%s]", packageId, name, description, products));
                aPackage.setName(name);
                aPackage.setDescription(description);
                aPackage.setProducts(Arrays.asList(products));

                PackageEntity packageEntity = null;
                try {
                    // update package
                    packageEntity = PackageMapper.INSTANCE.packageToEntity(aPackage);
                    this.packageRepository.save(packageEntity);
                    logger.info(String.format("Updated package: [%s]", packageEntity));
                    result = true;
                } catch (Exception e) {
                    this.logger.error(String.format("Something went wrong updating package: [%d] with object: [%s]", packageId, packageEntity), e);
                    throw e;
                }
            }
        } catch (PackageNotFoundException e) {
            throw new PackageNotFoundException(String.format("No package found with id: [%d] for update", packageId), e);
        } catch (Exception e) {
            throw new PackageServiceException(String.format("Something went wrong looking up package: [%d] for update", packageId), e);
        }

        return result;
    }

    @Override
    public boolean deletePackage(@NotNull final int packageId) throws PackageNotFoundException, PackageServiceException {
        Assert.isTrue(packageId > 0, "id of package is required for delete");
        boolean result = false;

        try {
            logger.debug(String.format("Deleting package with id: [%d]", packageId));
            this.packageRepository.deleteById(packageId);
            logger.info(String.format("Deleted package: [%d]", packageId));
            result = true;
        } catch (IllegalArgumentException | EmptyResultDataAccessException e) {
            throw new PackageNotFoundException(String.format("No package was found with id: [%d] to delete", packageId));
        } catch (Exception e) {
            throw new PackageServiceException(String.format("Something went wrong deleting package with id: [%d]", packageId), e);
        }

        return result;
    }


}
