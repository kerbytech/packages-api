package com.kerby.example.packages.service;

import com.kerby.example.database.models.PackageEntity;
import com.kerby.example.database.repositories.PackageRepository;
import com.kerby.example.packages.exceptions.PackageNotFoundException;
import com.kerby.example.packages.exceptions.PackageServiceException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.kerby.example.packages.models.Package;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootConfiguration
public class PackageServiceImplTest extends TestUtility {

    @Mock
    PackageRepository packageRepository;

    @Spy
    @InjectMocks
    PackageServiceImpl packageService;

    @Test
    public void when_createNewPackage_expectSuccess() {
        // mock saving of item
        final PackageEntity mockedResponse = new PackageEntity(null, null, null);
        mockedResponse.setId(1);
        Mockito.when(packageRepository.save(Mockito.any(PackageEntity.class))).thenReturn(mockedResponse);

        // create
        int packageId = 0;
        try {
            packageId = this.packageService.createPackage(
                    "Foo",
                    "Foo description",
                    PRODUCT_ALPHA, PRODUCT_BETA
                    );
        } catch (PackageServiceException e) {
            Assert.fail("No package was found");
        }

        // assert created
        Assert.assertEquals(mockedResponse.getId(), packageId);
    }

    @Test
    public void when_lookupPackage_expectSuccess() throws PackageNotFoundException, PackageServiceException {
        // mock retrieval of item
        final PackageEntity mockedResponse = new PackageEntity("Foo", "Foo test", Arrays.asList(PRODUCT_ENTITY_ALPHA));
        mockedResponse.setId(1);
        Mockito.when(packageRepository.findById(1)).thenReturn(Optional.of(mockedResponse));

        // lookup
        final Package aPackage = this.packageService.getPackage(null, 1);

        // assert created
        Assert.assertEquals(1, aPackage.getId());
        Assert.assertEquals("Foo", aPackage.getName());
        Assert.assertEquals("Foo test", aPackage.getDescription());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(PRODUCT_ALPHA)), aPackage.getProducts());
    }


    @Test
    public void when_updatePackageAnyValues_expectSuccess() throws PackageNotFoundException, PackageServiceException {
        // mock retrieval of item
        final PackageEntity mockedResponse = new PackageEntity("Foo", "Foo test", Arrays.asList(PRODUCT_ENTITY_ALPHA));
        mockedResponse.setId(1);
        Mockito.when(packageRepository.findById(1)).thenReturn(Optional.of(mockedResponse));

        this.packageService.updatePackage(
                1,
                "Foo updated",
                "Foo test updated",
                PRODUCT_ALPHA, PRODUCT_BETA
        );

        final ArgumentCaptor<PackageEntity> argument = ArgumentCaptor.forClass(PackageEntity.class);
        Mockito.verify(packageRepository).save(argument.capture());

        Assert.assertEquals("Foo updated", argument.getValue().getName());
        Assert.assertEquals("Foo test updated", argument.getValue().getDescription());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(PRODUCT_ENTITY_ALPHA, PRODUCT_ENTITY_BETA)), argument.getValue().getProductEntities());
    }

    @Test(expected = PackageNotFoundException.class)
    public void when_lookupInvalidPackageId_expectedException() throws PackageNotFoundException, PackageServiceException {
        // mock retrieval of item
        final PackageEntity mockedResponse = new PackageEntity("Foo", "Foo test", Arrays.asList(PRODUCT_ENTITY_ALPHA));
        mockedResponse.setId(1);
        Mockito.when(packageRepository.findById(1)).thenReturn(Optional.of(mockedResponse));

        this.packageService.getPackage(
                null,
                18
        );

        Assert.fail("Expected exception");
    }

    @Test
    public void when_shiftingCurrencyDenomination_expectCorrectResults() {
        Assert.assertThat(
                new BigDecimal(0),
                Matchers.comparesEqualTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(0)))
        );
        Assert.assertThat(
                new BigDecimal(0.01),
                Matchers.closeTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(1)), new BigDecimal(0.001))
        );
        Assert.assertThat(
                new BigDecimal(0.10),
                Matchers.closeTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(10)), new BigDecimal(0.001))
        );
        Assert.assertThat(
                new BigDecimal(1.00),
                Matchers.closeTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(100)), new BigDecimal(0.001))
        );
        Assert.assertThat(
                new BigDecimal(10.00),
                Matchers.closeTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(1000)), new BigDecimal(0.001))
        );
        Assert.assertThat(
                new BigDecimal(100.00),
                Matchers.closeTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(10000)), new BigDecimal(0.001))
        );
        Assert.assertThat(
                new BigDecimal(1000.00),
                Matchers.closeTo(this.packageService.shiftCurrencyDenomination(new BigDecimal(100000)), new BigDecimal(0.001))
        );


    }

    // TODO tests on currency conversion, invalid inputs, different character encoding etc
}
