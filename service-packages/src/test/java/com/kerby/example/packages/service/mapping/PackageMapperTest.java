package com.kerby.example.packages.service.mapping;

import com.kerby.example.database.models.PackageEntity;
import com.kerby.example.packages.mapping.PackageMapper;
import com.kerby.example.packages.models.Package;
import com.kerby.example.packages.service.TestUtility;
import org.hamcrest.Matchers;
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
public class PackageMapperTest extends TestUtility {

    @Test
    public void when_mappingPackageEntityToPackage_expectSuccess() {
        // repository entity
        final PackageEntity packageEntity = new PackageEntity(
                "Foo", "Foo description", Arrays.asList(PRODUCT_ENTITY_ALPHA, PRODUCT_ENTITY_BETA));
        packageEntity.setId(1);

        final Package aPackage = PackageMapper.INSTANCE.entityToPackage(packageEntity);

        Assert.assertEquals(1, aPackage.getId());
        Assert.assertEquals("Foo", aPackage.getName());
        Assert.assertEquals("Foo description", aPackage.getDescription());
        Assert.assertEquals( new ArrayList<>(Arrays.asList(PRODUCT_ALPHA, PRODUCT_BETA)), aPackage.getProducts());
        Assert.assertThat( new BigDecimal(250), Matchers.comparesEqualTo(aPackage.getPrice()));
    }

    @Test
    public void when_mappingEntityToPackage_expectSuccess() {
        // package pojo
        final Package aPackage = new Package("Foo", "Foo description", Arrays.asList(PRODUCT_ALPHA, PRODUCT_BETA));
        aPackage.setId(1);

        final PackageEntity packageEntity = PackageMapper.INSTANCE.packageToEntity(aPackage);

        Assert.assertEquals(1, packageEntity.getId());
        Assert.assertEquals("Foo", packageEntity.getName());
        Assert.assertEquals("Foo description", packageEntity.getDescription());
        Assert.assertEquals( new ArrayList<>(Arrays.asList(PRODUCT_ENTITY_ALPHA, PRODUCT_ENTITY_BETA)), packageEntity.getProductEntities());
    }

}
