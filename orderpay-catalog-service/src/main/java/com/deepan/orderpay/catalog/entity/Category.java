package com.deepan.orderpay.catalog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 100)
    private String name;

    @Column(length = 100)
    private String description;

    @OneToMany(mappedBy = "category")
    @Setter(AccessLevel.NONE)
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);      // keep BOTH sides in sync
    }

    public void removeProduct(Product product) {
        // not setting product.category to null: that column is NOT NULL,
        // so a removed product must be deleted (orphanRemoval) or moved to another category
        products.remove(product);
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
