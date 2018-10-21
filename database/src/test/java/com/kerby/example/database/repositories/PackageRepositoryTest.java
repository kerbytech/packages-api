package com.kerby.example.database.repositories;

import com.kerby.example.database.DatabaseConfig;
import com.kerby.example.database.repositories.PackageRepository;
import com.kerby.example.database.models.PackageEntity;
import com.kerby.example.database.models.ProductEntity;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {PackageRepository.class})
@Import(DatabaseConfig.class) // use embedded H2 repos
@DataJpaTest // ensure they are test instances
public class PackageRepositoryTest {

    private Integer packageFooId;
    private Integer packageBarId;
    private Integer packageBazId;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PackageRepository packageRepository;

    @Before
    public void beforeTest() {
        // setup database with dummy data
        packageFooId = (Integer) entityManager.persistAndGetId(new PackageEntity("Foo", "Foo description",
                Collections.singletonList(
                    new ProductEntity("alpha_id", "Alpha", new BigDecimal(100))
                )));
        packageBarId = (Integer) entityManager.persistAndGetId(new PackageEntity("Bar", "Bar description",
                Arrays.asList(
                    new ProductEntity("alpha_id", "Alpha", new BigDecimal(100)),
                    new ProductEntity("beta_id", "Beta", new BigDecimal(150))
                )));
        packageBazId = (Integer) entityManager.persistAndGetId(new PackageEntity("Baz", "Baz description",  Arrays.asList(
                    new ProductEntity("alpha_id", "Alpha", new BigDecimal(100)),
                    new ProductEntity("beta_id", "Beta", new BigDecimal(150)),
                    new ProductEntity("gamma_id", "Gamma", new BigDecimal(200))
                )));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void when_createPackageItShouldBeAvailableForRead_expectPackage() {
        /* create and validate new package Biz */
        final PackageEntity packageBiz = new PackageEntity("Biz", "Biz description",
                Collections.singletonList(
                    new ProductEntity("delta_id", "Delta", new BigDecimal(100))
                ));
        Assert.assertEquals(packageBiz, packageRepository.save(packageBiz));

        /* create and validate new package Boz */
        final PackageEntity packageBoz = new PackageEntity("Boz", "Boz description",
                Collections.singletonList(
                        new ProductEntity("delta_id", "Delta", new BigDecimal(100))
                ));
        Assert.assertEquals(packageBoz, packageRepository.save(packageBoz));

        final int packageBizId = packageBiz.getId();

        // assert lookup methods and object equality
        Assert.assertEquals(packageBiz, packageRepository.findById(packageBizId).orElse(null));
        // assert details (avoiding obj equals)
        Assert.assertEquals(packageBiz.getName(), packageRepository.findById(packageBizId).get().getName());
        Assert.assertEquals(packageBiz.getProductEntities(), packageRepository.findById(packageBizId).get().getProductEntities());

        // assert number of records
        Assert.assertEquals(5, packageRepository.count());
    }

    @Test
    public void when_updatePackageById_expectSuccess() {

        // get package by Id
        final PackageEntity packageFoo = packageRepository.findById(packageFooId).orElse(null);
        Assert.assertNotNull(packageFoo);

        // update details
        packageFoo.setName("Foo updated");
        packageFoo.setDescription("Foo description updated");
        // update products using override
        packageFoo.setProductEntities(new ArrayList<>(Arrays.asList(
                new ProductEntity("alpha_id", "Alpha", new BigDecimal(100)),
                new ProductEntity("beta_id", "Beta", new BigDecimal(150))
        )));
        // update products using append
        packageFoo.getProductEntities().add(new ProductEntity("gamma_id", "Gamma", new BigDecimal(200)));
        packageFoo.getProductEntities().add(new ProductEntity("epsilon_id", "Epsilon", new BigDecimal(250)));
        packageFoo.getProductEntities().add(new ProductEntity("zeta_id", "Zeta", new BigDecimal(300)));

        final PackageEntity newPackageFoo = packageRepository.save(packageFoo);
        // assert object equality
        Assert.assertEquals(packageFoo, newPackageFoo);
        // assert details have updated
        Assert.assertEquals("Foo updated", newPackageFoo.getName());
        Assert.assertEquals("Foo description updated", newPackageFoo.getDescription());

        // assert products were set (also ensures equality on id)
        final List<ProductEntity> assertionProductEntities = Arrays.asList(
                new ProductEntity("alpha_id"),
                new ProductEntity("beta_id"),
                new ProductEntity("gamma_id"),
                new ProductEntity("epsilon_id"),
                new ProductEntity("zeta_id")
        );
        Assert.assertEquals(assertionProductEntities, newPackageFoo.getProductEntities());

        // assert number of records
        Assert.assertEquals(3, packageRepository.count());
    }

    @Test
    public void when_deletePackageById_expectSuccess() {
        Assert.assertEquals(3, packageRepository.count());
        packageRepository.deleteById(packageFooId);
        Assert.assertEquals(2, packageRepository.count());
    }


}
