package com.kerby.example.api.controller;

import com.kerby.example.api.exceptions.ApiException;
import com.kerby.example.api.mapping.CurrencyDtoMapper;
import com.kerby.example.api.models.requests.PackageRequest;
import com.kerby.example.api.models.responses.CurrenciesResponse;
import com.kerby.example.api.mapping.PackageDtoMapper;
import com.kerby.example.api.mapping.ProductDtoMapper;
import com.kerby.example.api.models.dtos.CurrencyDto;
import com.kerby.example.api.models.dtos.PackageDto;
import com.kerby.example.api.models.responses.PackageResponse;
import com.kerby.example.api.models.responses.PackagesResponse;
import com.kerby.example.packages.exceptions.PackageNotFoundException;
import com.kerby.example.packages.exceptions.PackageServiceException;
import com.kerby.example.packages.models.Currency;
import com.kerby.example.packages.models.Product;
import com.kerby.example.packages.models.Package;
import com.kerby.example.packages.service.PackageService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for CRUD access to Packages, and Currency codes
 * Swagger documentation available at at /swagger-ui.html
 */
@RestController
@RequestMapping(value = "/packages-api")
public class PackagesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PackageService packageService;

    public PackagesController(@Autowired PackageService packageService) {
        this.packageService = packageService;
    }


    @ApiOperation(value = "Get currencies",
            notes = "Get all currencies available for exchange rate conversion. " +
                    "These contains both currency codes and human readable labels.")
    @RequestMapping(value = "/currency",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<CurrenciesResponse> getCurrencyCodes() throws ApiException {
        final ResponseEntity<CurrenciesResponse> result;

        try {
            // get available currencies
            final List<Currency> currencies = this.packageService.getCurrencies();
            final List<CurrencyDto> currencyDtos = currencies.stream()
                    .map(CurrencyDtoMapper.INSTANCE::currencyToDto)
                    .collect(Collectors.toList());
            // return 200 with currencies
            result = new ResponseEntity<>(new CurrenciesResponse(currencyDtos), HttpStatus.OK);

            // wrap and throw any exceptions to hide internal messages from checked and unchecked exceptions
        } catch (Exception e) {
            throw new ApiException("There was a problem handling your request to get currencies", e);
        }

        return result;
    }

    @ApiOperation(value = "Create package")
    @RequestMapping(value = "/package",
            method = RequestMethod.POST,
            produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<Integer> createPackage(@RequestBody() final PackageRequest packageRequest) throws ApiException {
        Assert.notNull(packageRequest, "Request body is required to create package");
        Assert.notNull(packageRequest.getPackageDto(), "Packages in request body are required to create package");
        final ResponseEntity<Integer> result;

        try {
            final PackageDto packageDto = packageRequest.getPackageDto();
            // create package
            final int packageId = this.packageService.createPackage(
                    packageDto.getName(),
                    packageDto.getDescription(),
                    packageDto.getProductDtos().stream().map(ProductDtoMapper.INSTANCE::dtoToProduct).toArray(Product[]::new));

            // return 201 with packageId
            result = new ResponseEntity<>(packageId, HttpStatus.CREATED);

            // wrap and throw any exceptions to hide internal messages from checked and unchecked exceptions
        } catch (PackageServiceException e) {
            this.logger.error(String.format("Something went wrong while creating new package in create package API endpoint. Request body: [%s]", packageRequest));
            throw new ApiException("There was a problem creating a new package", e);
        } catch (Exception e) {
            this.logger.error(String.format("Something went wrong in create package API endpoint. Request body: [%s]", packageRequest));
            throw new ApiException("There was a problem handling your request to create a package", e);
        }

        return result;
    }

    @ApiOperation(value = "Get packages")
    @RequestMapping(value = "/package",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity<PackagesResponse> getPackages(@RequestParam(value = "currency", required = false) final String currency) throws ApiException {
        final ResponseEntity<PackagesResponse> result;

        try {
            // get packages
            final List<Package> packages = this.packageService.getPackages(currency);
            if (packages != null && !packages.isEmpty()) {
                final List<PackageDto> packageDtos = packages
                        .stream()
                        .map(PackageDtoMapper.INSTANCE::packageToDto)
                        .collect(Collectors.toList());
                // return 200 with packages
                result = new ResponseEntity<>(new PackagesResponse(packageDtos), HttpStatus.OK);
            } else {
                // no packages - return empty 200
                result = new ResponseEntity<>(HttpStatus.OK);
            }

            // wrap and throw any exceptions to hide internal messages from checked and unchecked exceptions
        } catch (PackageServiceException e) {
            this.logger.error(String.format("Something went wrong looking up packages in read packages API endpoint. Currency param: [%s]", currency));
            throw new ApiException("There was a problem looking up packages", e);
        } catch (Exception e) {
            this.logger.error(String.format("Something went wrong in read packages API endpoint. Currency param: [%s]", currency));
            throw new ApiException("There was a problem handling your request to read packages", e);
        }

        return result;
    }

    @ApiOperation(value = "Get package")
    @RequestMapping(value = "/package/{id}", method = RequestMethod.GET)
    public ResponseEntity<PackageResponse> readPackage(@PathVariable("id") final int id,
                                                       @RequestParam(value = "currency", required = false) final String currency) throws ApiException, PackageNotFoundException {
        Assert.isTrue(id > 0, "id is required to lookup package");
        final ResponseEntity<PackageResponse> result;

        try {
            // get package
            final Package aPackage = this.packageService.getPackage(currency, id);
            final PackageResponse packageResponse = new PackageResponse(PackageDtoMapper.INSTANCE.packageToDto(aPackage));
            // return 200 with package
            result = new ResponseEntity<>(packageResponse, HttpStatus.OK);

            // wrap and throw any exceptions to hide internal messages from checked and unchecked exceptions
        } catch (PackageNotFoundException e) {
            throw new PackageNotFoundException(String.format("No package found with id [%d]", id), e);
        } catch (PackageServiceException e) {
            this.logger.error(String.format("Something went wrong looking up package in read package API endpoint. Id param: [%d], Currency param: [%s]", id, currency));
            throw new ApiException(String.format("There was a problem looking up package with id [%d]", id), e);
        } catch (Exception e) {
            this.logger.error(String.format("Something went wrong in read package API endpoint. Id param: [%d], Currency param: [%s]", id, currency));
            throw new ApiException(String.format("There was a problem handling your request to read package [%d]", id), e);
        }

        return result;
    }

    @ApiOperation(value = "Update package")
    @RequestMapping(value = "/package/{id}", method = RequestMethod.PUT)
    public ResponseEntity updatePackage(@PathVariable("id") final int id,
                                                 @RequestBody() final PackageRequest packageRequest) throws ApiException, PackageNotFoundException {
        Assert.isTrue(id > 0, "id is required to update package");
        Assert.notNull(packageRequest, "Request body is required to update package");
        final ResponseEntity result;

        final PackageDto packageDto = packageRequest.getPackageDto();
        try {
            // update package
            final boolean isUpdated = this.packageService.updatePackage(
                    id,
                    packageDto.getName(),
                    packageDto.getDescription(),
                    packageDto.getProductDtos().stream().map(ProductDtoMapper.INSTANCE::dtoToProduct).toArray(Product[]::new)
            );
            if (isUpdated) {
                // return 200 if success
                result = new ResponseEntity(HttpStatus.OK);
            } else {
                this.logger.error(String.format("Update failed with payload %s", packageRequest));
                throw new ApiException(String.format("There was a problem updating package [%s]", packageRequest));
            }

            // wrap and throw any exceptions to hide internal messages from checked and unchecked exceptions
        } catch (PackageNotFoundException e) {
            throw new PackageNotFoundException(String.format("No package found with id [%d]", id), e);
        } catch (PackageServiceException e) {
            this.logger.error(String.format("Something went wrong updating package in update package API endpoint. Request body: [%s]", packageRequest));
            throw new ApiException(String.format("There was a problem updating package with id [%d]", id), e);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            this.logger.error(String.format("Something went wrong in update package API endpoint. Request body: [%s]", packageRequest));
            throw new ApiException(String.format("There was a problem handling your request to update package [%s]", packageRequest.getPackageDto()), e);
        }

        return result;
    }

    @ApiOperation(value = "Delete package")
    @RequestMapping(value = "/package/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deletePackage(@PathVariable("id") final int id) throws ApiException, PackageNotFoundException {
        final ResponseEntity result;
        Assert.isTrue(id > 0, "id is required to delete package");

        try {
            // delete package
            final boolean isDeleted = this.packageService.deletePackage(id);
            if (isDeleted) {
                // return 200 on success
                result = new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                this.logger.error(String.format("Failed to delete id %d", id));
                throw new ApiException(String.format("There was a problem deleting package [%s]", id));
            }

            // wrap and throw any exceptions to hide internal messages from checked and unchecked exceptions
        } catch (PackageNotFoundException e) {
            throw new PackageNotFoundException(String.format("No package found with id [%d]", id), e);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            this.logger.error(String.format("Something went wrong in delete package API endpoint. Id: [%s]", id));
            throw new ApiException(String.format("There was a problem handling your request to delete package [%d]", id), e);
        }

        return result;
    }

}
