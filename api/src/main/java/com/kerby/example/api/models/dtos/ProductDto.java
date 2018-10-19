package com.kerby.example.api.models.dtos;

import java.math.BigDecimal;

public class ProductDto {

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

    public ProductDto() {
    }

    public ProductDto(String id, String name, BigDecimal usdPrice) {
        this.id = id;
        this.name = name;
        this.usdPrice = usdPrice;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", usdPrice=" + usdPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDto)) return false;

        ProductDto that = (ProductDto) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
