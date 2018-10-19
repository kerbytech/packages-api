package com.kerby.example.api.mapping;

import com.kerby.example.api.models.dtos.PackageDto;
import com.kerby.example.packages.models.Package;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageDtoMapper {

    PackageDtoMapper INSTANCE = Mappers.getMapper(PackageDtoMapper.class);

    @Mapping(source = "products", target = "productDtos")
    PackageDto packageToDto(Package aPackage);

    @Mapping(source = "productDtos", target = "products")
    Package dtoToPackage(PackageDto dto);

}
