package com.kerby.example.api.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kerby.example.api.models.dtos.PackageDto;

public class PackageRequest {

    @JsonProperty("package")
    private PackageDto packageDto;

    public PackageDto getPackageDto() {
        return packageDto;
    }

    public void setPackageDto(PackageDto packageDto) {
        this.packageDto = packageDto;
    }

    public PackageRequest() {
    }

    public PackageRequest(PackageDto packageDto) {
        this.packageDto = packageDto;
    }

    @Override
    public String toString() {
        return "UpdatePackageRequest{" +
                " packageDto=" + packageDto +
                '}';
    }

}
