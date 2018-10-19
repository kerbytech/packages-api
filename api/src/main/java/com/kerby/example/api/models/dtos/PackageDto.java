package com.kerby.example.api.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

public class PackageDto {

    @ApiModelProperty(readOnly = true) // only reveal in response
    private int id;
    private String name;
    private String description;
    @ApiModelProperty(readOnly = true) // only reveal in response
    private BigDecimal price;
    @JsonProperty("products")
    protected List<ProductDto> productDtos;

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

    public List<ProductDto> getProductDtos() {
        return productDtos;
    }

    public void setProductDtos(List<ProductDto> productDtos) {
        this.productDtos = productDtos;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PackageDto() {}

    public PackageDto(String name, String description, List<ProductDto> productDtos) {
        this.name = name;
        this.description = description;
        this.productDtos = productDtos;
    }

}
