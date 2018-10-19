package com.kerby.example.database.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Representation of the Product to store in H2.
 *
 * TODO This would make more sense to be a Entity and have a XToMany relationship with the Package.
 * The advantage of using Embedded means we don't have to worry about state management of Products unlike if they were their
 * own Entity; e.g upsert and attachment to Packages, deletion of orphans, transaction handling.
 * This means less business logic throughout services using the repositories and/or the implementation of a DAO layer.
 *
 * The main disadvantage is we will end up with duplicate Products being created in H2 if multiple packages reference the
 * same Product. Not only is this poor for resource we would also find updates difficult. Thankfully Products cannot
 * be updated.
 *
 * Considering this is a working example with time constraints, and not production, it should suffice. The trade off is
 * sufficient.
 */
@Embeddable
public class ProductEntity {

    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal usdPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(BigDecimal usdPrice) {
        this.usdPrice = usdPrice;
    }

    public ProductEntity() {
    }

    public ProductEntity(String id) {
        this.id = id;
    }

    public ProductEntity(String id, String name, BigDecimal usdPrice) {
        this.id = id;
        this.name = name;
        this.usdPrice = usdPrice;
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", usdPrice=" + usdPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductEntity that = (ProductEntity) o;

        // unique on id
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
