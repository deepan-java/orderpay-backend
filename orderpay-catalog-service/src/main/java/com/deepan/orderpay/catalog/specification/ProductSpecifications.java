package com.deepan.orderpay.catalog.specification;

import com.deepan.orderpay.catalog.dto.ProductSearchCriteria;
import com.deepan.orderpay.catalog.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> from(ProductSearchCriteria c) {
        List<Specification<Product>> specs = new ArrayList<>();

        if (c.name() != null && !c.name().isBlank()) {
            specs.add(nameContains(c.name().trim()));
        }
        if (c.categoryId() != null) {
            specs.add(inCategory(c.categoryId()));
        }
        if (c.minPrice() != null) {
            specs.add(priceAtLeast(c.minPrice()));
        }
        if (c.maxPrice() != null) {
            specs.add(priceAtMost(c.maxPrice()));
        }
        return Specification.allOf(specs);   // no filters = matches everything
    }

    static Specification<Product> fetchCategory() {
        return (root, query, cb) -> {
            if (Product.class.equals(query.getResultType())) {
                root.fetch("category", JoinType.INNER);
            }

            return cb.conjunction();
        };
    }
    static Specification<Product> nameContains(String name) {
        String pattern = "%" + escapeLike(name.toLowerCase()) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern, '\\');
    }

    static Specification<Product> inCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    static Specification<Product> priceAtLeast(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("price"), min);
    }

    static Specification<Product> priceAtMost(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), max);
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    static Specification<Product> inStock() {
        return (root, query, cb) -> cb.greaterThan(root.<Integer>get("stockQuantity"), 0);
    }
}