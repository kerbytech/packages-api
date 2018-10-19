package com.kerby.example.api.mapping;

import com.kerby.example.api.models.dtos.CurrencyDto;
import com.kerby.example.packages.models.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyDtoMapper {

    CurrencyDtoMapper INSTANCE = Mappers.getMapper(CurrencyDtoMapper.class);

    CurrencyDto currencyToDto(Currency currency);

    Currency dtoToCurrency(CurrencyDto dto);

}
