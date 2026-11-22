package com.deepan.orderpay.catalog.repository;

import com.deepan.orderpay.catalog.dto.CategoryPriceStats;
import com.deepan.orderpay.catalog.entity.Category;
import com.deepan.orderpay.catalog.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl
        implements ProductRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<CategoryPriceStats> priceStatsByCategory() {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Tuple> query =
                cb.createTupleQuery();

        Root<Product> product =
                query.from(Product.class);

        Join<Product, Category> category =
                product.join("category");

        Expression<BigDecimal> price =
                product.get("price");

        query.multiselect(
                        category.get("id").alias("categoryId"),
                        category.get("name").alias("categoryName"),
                        cb.min(price).alias("minPrice"),
                        cb.max(price).alias("maxPrice"),
                        cb.avg(price).alias("avgPrice"),
                        cb.count(product).alias("productCount"))
                .groupBy(
                        category.get("id"),
                        category.get("name"))
                .orderBy(
                        cb.asc(category.get("name")));

        return em.createQuery(query)
                .getResultList()
                .stream()
                .map(t -> new CategoryPriceStats(
                        t.get("categoryId", Long.class),
                        t.get("categoryName", String.class),
                        t.get("minPrice", BigDecimal.class),
                        t.get("maxPrice", BigDecimal.class),
                        BigDecimal.valueOf(
                                        t.get("avgPrice", Double.class))
                                .setScale(2, RoundingMode.HALF_UP),
                        t.get("productCount", Long.class)))
                .toList();
    }
}