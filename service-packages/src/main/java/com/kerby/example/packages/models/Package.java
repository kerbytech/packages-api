package com.kerby.example.packages.models;

import java.math.BigDecimal;
import java.util.List;

public class Package {

    private int id;
    private String name;
    private String description;
    private List<Product> products;
    private BigDecimal price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Package() {
    }

    public Package(String name, String description, List<Product> products) {
        this.name = name;
        this.description = description;
        this.products = products;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", products=" + products +
                ", price=" + price +
                '}';
    }
}
