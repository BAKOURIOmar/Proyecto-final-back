package com.babel.semilla.babel.practicafinal.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babel.semilla.babel.practicafinal.entities.UserCoin;

@Repository
public interface UserCoinRepository extends JpaRepository<UserCoin, Integer>{
	List<UserCoin> findByUser_Id(Integer userId);
}
