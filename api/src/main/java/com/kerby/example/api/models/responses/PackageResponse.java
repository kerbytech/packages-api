package com.kerby.example.api.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kerby.example.api.models.dtos.PackageDto;

public class PackageResponse {

    @JsonProperty("package")
    private PackageDto packageDto;

    public PackageDto getPackageDto() {
        return packageDto;
    }

    public void setPackageDto(PackageDto packageDto) {
        this.packageDto = packageDto;
    }

    public PackageResponse(PackageDto packageDto) {
        this.packageDto = packageDto;
    }
}
