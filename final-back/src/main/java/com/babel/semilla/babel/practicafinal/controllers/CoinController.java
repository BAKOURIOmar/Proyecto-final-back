package com.babel.semilla.babel.practicafinal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.babel.semilla.babel.practicafinal.dtos.CoinDTO;
import com.babel.semilla.babel.practicafinal.services.CoinService;

@RestController
@RequestMapping("/coins")
public class CoinController {
	
	@Autowired
    private CoinService coinservice;
	
	@PostMapping
	public ResponseEntity<CoinDTO> createCoin(@RequestBody CoinDTO coinDTO) {
	   return ResponseEntity.ok(coinservice.createCoin(coinDTO));
	}
}
