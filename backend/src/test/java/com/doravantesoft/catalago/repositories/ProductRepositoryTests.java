package com.doravantesoft.catalago.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.doravantesoft.catalago.entities.Product;
import com.doravantesoft.catalago.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;
	
	private long existentId;
	private long nonExistentId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existentId = 1L;
		nonExistentId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findByIdShouldReturnNotNullWhenIdExist() {
		
		Optional <Product> result = repository.findById(existentId); 
		
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnNullWhenIdNonExist() {
		
		Optional <Product> result = repository.findById(nonExistentId); 
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void saveShouldPersitWhithAutoIncrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		
		repository.deleteById(existentId);
		
		Optional <Product> result = repository.findById(existentId);
		
		Assertions.assertFalse(result.isPresent());
	}
}
