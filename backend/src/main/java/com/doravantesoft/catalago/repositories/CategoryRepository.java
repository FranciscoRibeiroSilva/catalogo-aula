package com.doravantesoft.catalago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doravantesoft.catalago.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
