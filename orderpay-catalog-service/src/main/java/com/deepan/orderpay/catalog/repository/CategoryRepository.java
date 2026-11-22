package com.deepan.orderpay.catalog.repository;

import com.deepan.orderpay.catalog.dto.CategoryProductCount;
import com.deepan.orderpay.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    @Query("""
        select new com.deepan.orderpay.catalog.dto.CategoryProductCount(
            c.id, c.name, count(p)
        )
        from Category c
        left join c.products p
        group by c.id, c.name
        order by count(p) desc, c.name
        """)
    List<CategoryProductCount> findProductCounts();
}
