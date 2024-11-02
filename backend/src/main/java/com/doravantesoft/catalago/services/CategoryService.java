package com.doravantesoft.catalago.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doravantesoft.catalago.DTO.CategoryDTO;
import com.doravantesoft.catalago.entities.Category;
import com.doravantesoft.catalago.repositories.CategoryRepository;
import com.doravantesoft.catalago.services.exceptions.DatabaseException;
import com.doravantesoft.catalago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)//ou faz tudo ou não faz nada (readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable){
		Page <Category> list = repository.findAll(pageable);
		
		return list.map(x -> new CategoryDTO(x));
		
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
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity = repository.getReferenceById(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);			
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













