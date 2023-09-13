package com.babel.semilla.babel.practicafinal.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.babel.semilla.babel.practicafinal.entities.Transaction;
import com.babel.semilla.babel.practicafinal.entities.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	List<Transaction> findByDateAndCoin_IdAndTypeAndUser_Id(Date fecha,Integer coinId, TransactionType tipoOperacion,Integer userId);
	List<Transaction> findByDateAndCoin_IdAndUser_Id(Date fecha,Integer coinId,Integer userId);
	List<Transaction> findByDateAndTypeAndUser_Id(Date fecha,TransactionType tipoOperacion,Integer userId);
	List<Transaction> findByDateAndUser_Id(Date fecha,Integer userId);
	List<Transaction> findByCoin_IdAndTypeAndUser_Id(Integer coinId, TransactionType tipoOperacion,Integer userId );
	List<Transaction> findByCoin_IdAndUser_Id(Integer coinId, Integer userId);
	List<Transaction> findByTypeAndUser_Id(TransactionType tipoOperacion,Integer userId);
	List<Transaction> findByUser_Id(Integer userId);

}
