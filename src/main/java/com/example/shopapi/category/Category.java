package com.example.shopapi.category;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.discounts.interfaces.DiscountOwner;
import com.example.shopapi.discounts.categoryDiscounts.CategoryDiscount;
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
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity implements DiscountOwner<CategoryDiscount> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_id"
    )
    private Category parent;

    @OneToMany(
            mappedBy = "parent",
            fetch = FetchType.LAZY
    )
    private List<Category> children = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY
    )
    private List<Product> products = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CategoryDiscount> discounts =
            new ArrayList<>();

    @Override
    public void addDiscount(
            CategoryDiscount discount
    ){
        discounts.add(
                discount
        );

        discount.setCategory(
                this
        );
    }

}