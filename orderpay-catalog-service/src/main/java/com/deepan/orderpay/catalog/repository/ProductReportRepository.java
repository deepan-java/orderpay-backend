package com.deepan.orderpay.catalog.repository;

import com.deepan.orderpay.catalog.dto.CategoryStockValue;
import com.deepan.orderpay.catalog.dto.InventorySummary;
import com.deepan.orderpay.catalog.dto.TopProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductReportRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public InventorySummary inventorySummary() {
        String sql = """
                select count(*)                                  as product_count,
                       coalesce(sum(stock_quantity), 0)          as total_units,
                       coalesce(sum(price * stock_quantity), 0)  as total_value,
                       coalesce(round(avg(price), 2), 0)         as average_price
                from products
                """;
        return jdbc.queryForObject(sql, Map.of(), (rs, i) -> new InventorySummary(
                rs.getLong("product_count"),
                rs.getLong("total_units"),
                rs.getBigDecimal("total_value"),
                rs.getBigDecimal("average_price")));
    }

    public List<CategoryStockValue> stockValueByCategory() {
        String sql = """
                select c.id, c.name,
                       count(p.id)                               as product_count,
                       coalesce(sum(p.stock_quantity), 0)        as total_units,
                       coalesce(sum(p.price * p.stock_quantity), 0) as stock_value
                from categories c
                left join products p on p.category_id = c.id
                group by c.id, c.name
                order by stock_value desc, c.name
                """;
        return jdbc.query(sql, (rs, i) -> new CategoryStockValue(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("product_count"),
                rs.getLong("total_units"),
                rs.getBigDecimal("stock_value")));
    }

    public List<TopProduct> topProductsPerCategory(int n) {
        String sql = """
                select * from (
                    select c.id as category_id, c.name as category_name,
                           p.id as product_id, p.name as product_name, p.price,
                           row_number() over (partition by c.id order by p.price desc, p.id) as rnk
                    from products p
                    join categories c on c.id = p.category_id
                ) ranked
                where rnk <= :n
                order by category_name, rnk
                """;
        return jdbc.query(sql, new MapSqlParameterSource("n", n), (rs, i) -> new TopProduct(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("price"),
                rs.getInt("rnk")));
    }
}
