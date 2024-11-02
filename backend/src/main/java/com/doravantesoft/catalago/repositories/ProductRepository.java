package com.doravantesoft.catalago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doravantesoft.catalago.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
