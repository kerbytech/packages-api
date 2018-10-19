package com.kerby.example.api.mapping;

import com.kerby.example.api.TestUtility;
import com.kerby.example.api.models.dtos.PackageDto;
import com.kerby.example.packages.models.Package;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootConfiguration
public class PackageDtoMapperTest extends TestUtility {

    @Test
    public void when_mappingPackageDtoToPackage_expectSuccess() {
        // dto
        final PackageDto dto =
                new PackageDto(
                        "Foo",
                        "Foo description",
                        Arrays.asList(
                                TestUtility.PRODUCT_DTO_ALPHA, TestUtility.PRODUCT_DTO_BETA
                        )
                );
        dto.setId(1);
        dto.setPrice(new BigDecimal(10.00));

        final Package aPackage = PackageDtoMapper.INSTANCE.dtoToPackage(dto);

        Assert.assertEquals(1, aPackage.getId());
        Assert.assertEquals("Foo", aPackage.getName());
        Assert.assertEquals("Foo description", aPackage.getDescription());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(TestUtility.PRODUCT_ALPHA, TestUtility.PRODUCT_BETA)), aPackage.getProducts());
        Assert.assertEquals(new BigDecimal(10.00), aPackage.getPrice());
    }

    @Test
    public void when_mappingPackageToPackageDto_expectSuccess() {
        // package pojo
        final Package aPackage = new Package(
                "Foo",
                "Foo description",
                Arrays.asList(
                        TestUtility.PRODUCT_ALPHA, TestUtility.PRODUCT_BETA
                ));
        aPackage.setId(1);
        aPackage.setPrice(new BigDecimal(10.00));

        final PackageDto dto = PackageDtoMapper.INSTANCE.packageToDto(aPackage);

        Assert.assertEquals(1, dto.getId());
        Assert.assertEquals("Foo", dto.getName());
        Assert.assertEquals("Foo description", dto.getDescription());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(TestUtility.PRODUCT_DTO_ALPHA, TestUtility.PRODUCT_DTO_BETA)), dto.getProductDtos());
        Assert.assertEquals(new BigDecimal(10.00), dto.getPrice());

    }

}
