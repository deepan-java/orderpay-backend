package com.deepan.orderpay.catalog;

import com.deepan.orderpay.catalog.dto.ProductSearchCriteria;
import com.deepan.orderpay.catalog.entity.Category;
import com.deepan.orderpay.catalog.entity.Product;
import com.deepan.orderpay.catalog.service.ProductService;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductQueryCountTest {

    @Autowired
    EntityManager em;

    @Autowired
    ProductService productService;

    private Statistics prepare() {

        for (int i = 1; i <= 10; i++) {

            Category category = new Category(
                    "QC-" + System.nanoTime() + "-" + i,
                    "qc"
            );

            em.persist(category);

            em.persist(new Product(
                    "QC Product " + i,
                    "qc",
                    BigDecimal.TEN,
                    i,
                    category
            ));
        }

        em.flush();
        em.clear();

        Statistics stats = em.getEntityManagerFactory()
                .unwrap(SessionFactory.class)
                .getStatistics();

        stats.setStatisticsEnabled(true);
        stats.clear();

        return stats;
    }

    @Test
    void searchPageOfTenProducts_usesAtMostThreeStatements() {

        Statistics stats = prepare();

        productService.search(
                new ProductSearchCriteria(
                        "QC Product",
                        null,
                        null,
                        null
                ),
                PageRequest.of(0, 10)
        );

        long statements = stats.getPrepareStatementCount();

        System.out.println(
                ">>> SQL statements for one page of 10 products: "
                        + statements
        );

        assertThat(statements)
                .as("SQL statements for one page of 10 products")
                .isLessThanOrEqualTo(3);
    }
}