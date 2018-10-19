package com.kerby.example.packages.service;

import com.kerby.example.database.models.ProductEntity;
import com.kerby.example.packages.models.Product;

import java.math.BigDecimal;

public class TestUtility {

    // reusable products to use throughout tests without repetition
    protected static final Product PRODUCT_ALPHA = new Product("alpha_1", "Alpha", new BigDecimal(10.00));
    protected static final Product PRODUCT_BETA = new Product("beta_1", "Beta", new BigDecimal(15.00));

    protected static final ProductEntity PRODUCT_ENTITY_ALPHA = new ProductEntity("alpha_1", "Alpha", new BigDecimal(10.00));
    protected static final ProductEntity PRODUCT_ENTITY_BETA = new ProductEntity("beta_1", "Beta", new BigDecimal(15.00));

}
