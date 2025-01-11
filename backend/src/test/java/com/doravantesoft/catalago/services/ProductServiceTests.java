package com.doravantesoft.catalago.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.doravantesoft.catalago.DTO.ProductDTO;
import com.doravantesoft.catalago.entities.Category;
import com.doravantesoft.catalago.entities.Product;
import com.doravantesoft.catalago.repositories.CategoryRepository;
import com.doravantesoft.catalago.repositories.ProductRepository;
import com.doravantesoft.catalago.services.exceptions.DatabaseException;
import com.doravantesoft.catalago.services.exceptions.ResourceNotFoundException;
import com.doravantesoft.catalago.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

//Anotação para teste de unidade
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRespository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	
	//page para que é utilizada em teste
	private PageImpl <Product> page;
	private Category category;
	private Product product;
	private ProductDTO dto;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		category = Factory.createCategory();
		dependentId = 3L;
		product = Factory.createProduct();
		dto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		//Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(categoryRespository.getReferenceById(existingId)).thenReturn(category);
		Mockito.when(categoryRespository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);;
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		
	}
	//aula 0a57 29
	
	@Test
	public void updateShouldThrowsEntityNotFoundExceptionWhenIdDoesExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dto);
		});
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.update(existingId, dto);
		
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionwhenIdNonExtists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(nonExistingId);
		});
		
		Mockito.verify(repository).findById(nonExistingId);
	}
	
	@Test
	public void findByIdShouldProductDTOwhenIdExtists() {
		
		ProductDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		//Mockito.verify(repository).findById(existingId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	public void deleteShouldThrowDataIntegrityViolationExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void deleteShouldDeleteDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);

	}
}
