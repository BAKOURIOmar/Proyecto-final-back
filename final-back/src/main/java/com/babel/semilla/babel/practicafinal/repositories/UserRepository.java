package com.babel.semilla.babel.practicafinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babel.semilla.babel.practicafinal.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByEmail(String email);
	boolean existsByEmail(String email);
}

