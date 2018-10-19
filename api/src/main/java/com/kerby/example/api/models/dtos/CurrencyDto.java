package com.kerby.example.api.models.dtos;

public class CurrencyDto {

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CurrencyDto() {
    }

    public CurrencyDto(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
