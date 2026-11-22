package com.deepan.orderpay.catalog.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false , length = 150)
    private String name;

    @Column(length = 1500)
    private String description;

    @Column(nullable = false ,precision = 12 , scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity" , nullable = false)
    private int stockQuantity;

    @Version
    @Setter(AccessLevel.NONE)
    private long version;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "category_id" ,nullable = false)
    private Category category;

    public Product(String name, String description, BigDecimal price, int stockQuantity, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        if (stockQuantity < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock"
            );
        }

        this.stockQuantity -= quantity;
    }


}