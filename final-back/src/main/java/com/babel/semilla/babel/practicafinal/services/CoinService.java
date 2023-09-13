package com.babel.semilla.babel.practicafinal.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.babel.semilla.babel.practicafinal.dtos.CoinDTO;
import com.babel.semilla.babel.practicafinal.entities.Coin;
import com.babel.semilla.babel.practicafinal.entities.User;
import com.babel.semilla.babel.practicafinal.repositories.CoinRepository;
import com.babel.semilla.babel.practicafinal.repositories.UserRepository;

@Service
public class CoinService {
	
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public List<CoinDTO> getCoins(){
		List<Coin> coins = coinRepository.findAll();
		 return coins.stream()
	                .map(coin -> modelMapper.map(coin, CoinDTO.class))
	                .collect(Collectors.toList());
	}
	
	public CoinDTO getCoinById(Integer id){
	Optional<Coin> coinOptional = coinRepository.findById(id);
	if (coinOptional.isPresent()) {
		Coin coin = coinOptional.get();
		return modelMapper.map(coin,CoinDTO.class);
	} else {
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Moneda con ID %d no encontrada", id));
	}
	}
	
	public CoinDTO createCoin(CoinDTO coinDTO) {
		Coin coin = modelMapper.map(coinDTO,Coin.class);
		Coin createdCoin = coinRepository.save(coin);
		return modelMapper.map(createdCoin,CoinDTO.class);
	}
	
	
}
