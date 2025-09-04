package com.doravantesoft.catalago.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doravantesoft.catalago.entities.Product;
import com.doravantesoft.catalago.projections.ProductProjection;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	/**
	 *  SELECT DISTINCT tb_product.id, tb_product.name,
		FROM tb_product 
		INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
		WHERE tb_product_category.category_id IN (1, 3)
		AND LOWER (tb_product.name) LIKE '%ma%'
		ORDER BY tb_product.name
	*/
	@Query(nativeQuery = true, value = """
		SELECT * FROM(
        SELECT DISTINCT tb_product.id, tb_product.name
		FROM tb_product 
		INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
		WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
		AND LOWER (tb_product.name) LIKE LOWER (CONCAT('%',:name,'%'))
		) AS tb_result
			""", countQuery = """
		SELECT COUNT(*) FROM(
		SELECT DISTINCT tb_product.id, tb_product.name
		FROM tb_product 
		INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
		WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
		AND LOWER (tb_product.name) LIKE LOWER (CONCAT('%',:name,'%'))
		) AS tb_result
		""")
	Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);
	
	@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id IN :productIds")
	List<Product> searchProductWithCategories(List<Long> productIds);
}
