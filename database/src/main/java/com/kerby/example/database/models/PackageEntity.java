package com.kerby.example.database.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column
    private String name;
    @Column
    private String description;

    @ElementCollection
    @Embedded
    private List<ProductEntity> productEntities = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }

    public PackageEntity() {}

    public PackageEntity(String name, String description, List<ProductEntity> productEntities) {
        this.name = name;
        this.description = description;
        this.productEntities = productEntities;
    }

    @Override
    public String toString() {
        return "PackageEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", productEntities=" + productEntities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageEntity that = (PackageEntity) o;

        // unique on id
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
