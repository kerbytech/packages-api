package com.kerby.example.packages.mapping;

import com.kerby.example.database.models.ProductEntity;
import com.kerby.example.packages.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product entityToProduct(ProductEntity entity);

    ProductEntity productToEntity(Product entity);

}
