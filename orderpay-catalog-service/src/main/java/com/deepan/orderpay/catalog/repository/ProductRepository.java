package com.deepan.orderpay.catalog.repository;

import com.deepan.orderpay.catalog.dto.CheapestProductRow;
import com.deepan.orderpay.catalog.dto.ProductResponse;
import com.deepan.orderpay.catalog.entity.Category;
import com.deepan.orderpay.catalog.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product>,ProductRepositoryCustom {
    boolean existsByCategoryId(Long categoryId);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId, Pageable pageable);
    @Query(value = """
        select category_id   as "categoryId",
               category_name as "categoryName",
               product_id    as "productId",
               product_name  as "productName",
               price         as "price"
        from (
            select c.id as category_id,
                   c.name as category_name,
                   p.id as product_id,
                   p.name as product_name,
                   p.price as price,
                   row_number() over (
                       partition by c.id
                       order by p.price asc, p.id asc
                   ) as rn
            from products p
            join categories c on c.id = p.category_id
        ) ranked
        where rn = 1
        order by category_name
        """, nativeQuery = true)
    List<CheapestProductRow> findCheapestPerCategory();

    @Query(
            value = """
        select p
        from Product p
        join fetch p.category
        where p.stockQuantity <= :threshold
        order by p.stockQuantity asc, p.id asc
        """,
            countQuery = """
        select count(p)
        from Product p
        where p.stockQuantity <= :threshold
        """
    )
    Page<Product> findLowStock(
            @Param("threshold") int threshold,
            Pageable pageable
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""

            update Product p
                                         set p.price = round(p.price * :factor, 2),
                                             p.updatedAt = :now
                                         where p.category.id = :categoryId
    """)
    int adjustPriceByCategory(
            @Param("categoryId") Long categoryId,
            @Param("factor") BigDecimal factor,
            @Param("now") Instant now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update versioned Product p
        set p.stockQuantity = p.stockQuantity - :quantity,
            p.updatedAt = :now
        where p.id = :id
          and p.stockQuantity >= :quantity
        """)
    int decrementStockIfAvailable(
            @Param("id") Long id,
            @Param("quantity") int quantity,
            @Param("now") Instant now
    );

    @Query("select p.stockQuantity from Product p where p.id = :id")
    int findStockById(@Param("id") Long id);

}
