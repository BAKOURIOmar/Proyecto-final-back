package com.babel.semilla.babel.practicafinal.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.babel.semilla.babel.practicafinal.dtos.TransactionDTO;
import com.babel.semilla.babel.practicafinal.entities.Coin;
import com.babel.semilla.babel.practicafinal.entities.Transaction;
import com.babel.semilla.babel.practicafinal.entities.TransactionType;
import com.babel.semilla.babel.practicafinal.entities.User;
import com.babel.semilla.babel.practicafinal.repositories.CoinRepository;
import com.babel.semilla.babel.practicafinal.repositories.TransactionRepository;
import com.babel.semilla.babel.practicafinal.repositories.UserRepository;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository ;
	
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public List<TransactionDTO> filtertransactions(Date fecha, String nameCoin, TransactionType tipoOperacion, String userEmail) {
		  User user = userRepository.findByEmail(userEmail);

		    if (user == null) {
		        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("El usuario con el email %s no se ha encontrado", userEmail));
		    }

		Optional<Coin> coin = coinRepository.findByName(nameCoin);
                
       
		  List<Transaction> filteredTransactions;

		  if (fecha != null) {
		        if (coin.isPresent() && tipoOperacion != null) {
		            
		            filteredTransactions = transactionRepository.findByDateAndCoin_IdAndTypeAndUser_Id(
		                fecha, coin.get().getId(), tipoOperacion, user.getId());
		        } else if (coin.isPresent()) {
		            
		            filteredTransactions = transactionRepository.findByDateAndCoin_IdAndUser_Id(
		                fecha, coin.get().getId(), user.getId());
		        } else if (tipoOperacion != null) {
		            
		            filteredTransactions = transactionRepository.findByDateAndTypeAndUser_Id(
		                fecha, tipoOperacion, user.getId());
		        } else {
		            
		            filteredTransactions = transactionRepository.findByDateAndUser_Id(fecha, user.getId());
		        }
		    } else if (coin.isPresent() && tipoOperacion != null) {
		        
		        filteredTransactions = transactionRepository.findByCoin_IdAndTypeAndUser_Id(
		            coin.get().getId(), tipoOperacion, user.getId());
		    } else if (coin.isPresent()) {
		        
		        filteredTransactions = transactionRepository.findByCoin_IdAndUser_Id(coin.get().getId(), user.getId());
		    } else if (tipoOperacion != null) {
		        
		        filteredTransactions = transactionRepository.findByTypeAndUser_Id(tipoOperacion, user.getId());
		    } else {
		        
		        filteredTransactions = transactionRepository.findByUser_Id(user.getId());
		    }

		    return filteredTransactions.stream()
	                .map(filteredTransaction -> modelMapper.map(filteredTransaction, TransactionDTO.class))
	                .collect(Collectors.toList());
	}
}
