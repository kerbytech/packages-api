package com.kerby.example.packages.mapping;

import com.kerby.example.database.models.PackageEntity;
import com.kerby.example.database.models.ProductEntity;
import com.kerby.example.packages.models.Product;
import com.kerby.example.packages.models.Package;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PackageMapper {

    PackageMapper INSTANCE = Mappers.getMapper(PackageMapper.class);

    @Mapping(source = "productEntities", target = "products")
    default Package entityToPackage(PackageEntity entity) {
        if ( entity == null ) {
            return null;
        }
        final Package result;

        BigDecimal usdTotal = new BigDecimal(0);
        final List<Product> products = new ArrayList<>();
        for (ProductEntity productEntity : entity.getProductEntities()) {
            products.add(ProductMapper.INSTANCE.entityToProduct(productEntity));
            usdTotal = usdTotal.add(productEntity.getUsdPrice());
        }

        result = new Package(
                entity.getName(),
                entity.getDescription(),
                products);
        result.setId(entity.getId());
        result.setPrice(usdTotal);

        return result;
    }

    @Mapping(source = "products", target = "productEntities")
    PackageEntity packageToEntity(Package entity);

}
