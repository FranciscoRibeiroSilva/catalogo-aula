package com.doravantesoft.catalago.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doravantesoft.catalago.DTO.ProductDTO;
import com.doravantesoft.catalago.entities.Product;
import com.doravantesoft.catalago.repositories.ProductRepository;
import com.doravantesoft.catalago.services.exceptions.DatabaseException;
import com.doravantesoft.catalago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)//ou faz tudo ou não faz nada (readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page <Product> list = repository.findAll(pageRequest);
		
		return list.map(x -> new ProductDTO(x));
		
		//transforma a lista de categoria em uma stream, e para cada elemento da stream um novo CategoryDTO é instanciado
		//depois usando o collerct é transforma de novo em list e retornado como uma lista de CategoyDTO
		//return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		
		/**
		List <CategoryDTO> listDto = new ArrayList<>();
		for (Category cat : list) {
			listDto.add(new CategoryDTO(cat));
		}
		return listDto;
		*/
	}
		
	/**
	 *Optional evita o trabalho com valores nulos
	 */
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repository.getReferenceById(id);
			//entity.setName(dto.getName());
			entity = repository.save(entity);
			return new ProductDTO(entity);			
		}
		catch(EntityNotFoundException e){
			throw new ResourceNotFoundException("id not found "+id);
		}
	}
	//@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException("resource not found");
		}
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e){
			throw new DatabaseException("integrity violation");
		}
		
	}


}













