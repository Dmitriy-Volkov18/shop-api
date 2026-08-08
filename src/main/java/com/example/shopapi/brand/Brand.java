package com.example.shopapi.brand;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.discounts.interfaces.DiscountOwner;
import com.example.shopapi.discounts.brandDiscounts.BrandDiscount;
import com.example.shopapi.product.entities.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "brands",
        indexes = {
                @Index(
                        name = "idx_brand_name",
                        columnList = "name",
                        unique = true
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends BaseEntity implements DiscountOwner<BrandDiscount> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 300)
    private String website;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(
            mappedBy = "brand"
    )
    private List<Product> products = new ArrayList<>();

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addProduct(
            Product product
    ) {
        products.add(product);
        product.setBrand(this);
    }

    public void removeProduct(
            Product product
    ) {
        products.remove(product);
        product.setBrand(null);
    }

    @OneToMany(
            mappedBy = "brand",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BrandDiscount> discounts = new ArrayList<>();

    @Override
    public void addDiscount(
            BrandDiscount discount
    ){
        discounts.add(
                discount
        );

        discount.setBrand(
                this
        );
    }
}