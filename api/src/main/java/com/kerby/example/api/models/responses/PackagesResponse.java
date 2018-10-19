package com.kerby.example.api.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kerby.example.api.models.dtos.PackageDto;

import java.util.List;

public class PackagesResponse {

    @JsonProperty("packages")
    private List<PackageDto> packageDtos;

    public List<PackageDto> getPackageDtos() {
        return packageDtos;
    }

    public void setPackageDtos(List<PackageDto> packageDtos) {
        this.packageDtos = packageDtos;
    }

    public PackagesResponse(List<PackageDto> packageDtos) {
        this.packageDtos = packageDtos;
    }

    @Override
    public String toString() {
        return "PackagesResponse{" +
                "packageDtos=" + packageDtos +
                '}';
    }
}
