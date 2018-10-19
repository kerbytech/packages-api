package com.kerby.example.api.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kerby.example.api.models.dtos.CurrencyDto;

import java.util.List;

public class CurrenciesResponse {

    @JsonProperty("currencies")
    private List<CurrencyDto> currencyDtos;

    public List<CurrencyDto> getCurrencyDtos() {
        return currencyDtos;
    }

    public void setCurrencyDtos(List<CurrencyDto> currencyDtos) {
        this.currencyDtos = currencyDtos;
    }

    public CurrenciesResponse(List<CurrencyDto> currencyDtos) {
        this.currencyDtos = currencyDtos;
    }

    @Override
    public String toString() {
        return "CurrenciesResponse{" +
                "currencyDtos=" + currencyDtos +
                '}';
    }
}
