package com.doravantesoft.catalago.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doravantesoft.catalago.DTO.CategoryDTO;
import com.doravantesoft.catalago.entities.Category;
import com.doravantesoft.catalago.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)//ou faz tudo ou não faz nada (readOnly = true)
	public List<CategoryDTO> findAll(){
		List <Category> list = repository.findAll();
		
		//transforma a lista de categoria em uma stream, e para cada elemento da stream um novo CategoryDTO é instanciado
		//depois usando o collerct é transforma de novo em list e retornado como uma lista de CategoyDTO
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		
		/**
		List <CategoryDTO> listDto = new ArrayList<>();
		for (Category cat : list) {
			listDto.add(new CategoryDTO(cat));
		}
		return listDto;
		*/
	}

}
