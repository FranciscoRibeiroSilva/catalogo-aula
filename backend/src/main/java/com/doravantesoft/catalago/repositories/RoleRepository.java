package com.doravantesoft.catalago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doravantesoft.catalago.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
