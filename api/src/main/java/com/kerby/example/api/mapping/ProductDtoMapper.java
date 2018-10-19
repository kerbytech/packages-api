package com.kerby.example.api.mapping;

import com.kerby.example.api.models.dtos.ProductDto;
import com.kerby.example.packages.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductDtoMapper {

    ProductDtoMapper INSTANCE = Mappers.getMapper(ProductDtoMapper.class);

    ProductDto productToDto(Product aPackage);

    Product dtoToProduct(ProductDto dto);

}
