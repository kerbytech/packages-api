package com.kerby.example.api;

import com.kerby.example.api.models.dtos.ProductDto;
import com.kerby.example.packages.models.Product;

import java.math.BigDecimal;

public class TestUtility {

    // reusable products to use throughout tests without repetition
    protected static final Product PRODUCT_ALPHA = new Product("alpha_1", "Alpha", new BigDecimal(10.00));
    protected static final Product PRODUCT_BETA = new Product("beta_1", "Beta", new BigDecimal(15.00));

    protected static final ProductDto PRODUCT_DTO_ALPHA = new ProductDto("alpha_1", "Alpha", new BigDecimal(10.00));
    protected static final ProductDto PRODUCT_DTO_BETA = new ProductDto("beta_1", "Beta", new BigDecimal(15.00));

}
