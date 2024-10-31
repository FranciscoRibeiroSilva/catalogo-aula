package com.doravantesoft.catalago.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doravantesoft.catalago.entities.Category;
import com.doravantesoft.catalago.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)//ou faz tudo ou não faz nada (readOnly = true)
	public List<Category> findAll(){
		return repository.findAll();
	}

}
