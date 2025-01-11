package com.doravantesoft.catalago.tests;

import java.time.Instant;

import com.doravantesoft.catalago.DTO.ProductDTO;
import com.doravantesoft.catalago.entities.Category;
import com.doravantesoft.catalago.entities.Product;

public class Factory {
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.com", Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(new Category(2L, "Eletronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new  ProductDTO(product, product.getCategories());
		
	}
	
	public static Category createCategory() {
		return new Category(2L, "Eletronics");
	}
}
