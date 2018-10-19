package com.kerby.example.packages.models;

import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;

public class Product {

    private String id;
    private String name;
    private BigDecimal usdPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(BigDecimal usdPrice) {
        this.usdPrice = usdPrice;
    }

    public boolean isValid() {
        return ObjectUtils.allNotNull(this.id, this.name, this.usdPrice);
    }

    public Product() {
    }

    public Product(String id, String name, BigDecimal usdPrice) {
        this.id = id;
        this.name = name;
        this.usdPrice = usdPrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", usdPrice=" + usdPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        // equality on id
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
