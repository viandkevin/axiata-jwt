package com.axiata.jwt.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.axiata.jwt.model.DAOUser;

@Repository
public interface UserDao extends CrudRepository<DAOUser, Integer> {
	
	DAOUser findByUsername(String username);
	
	DAOUser findByEmployeeNumber(Integer employeeNumber);
	
	Page<DAOUser> findAll(Pageable pageable);
	
	Iterable<DAOUser> findAll(Sort sort);
	
}