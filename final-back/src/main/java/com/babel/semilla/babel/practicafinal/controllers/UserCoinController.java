package com.babel.semilla.babel.practicafinal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.babel.semilla.babel.practicafinal.dtos.UserCoinDTO;
import com.babel.semilla.babel.practicafinal.services.UserCoinService;

@RestController
@RequestMapping("/usercoin")
public class UserCoinController {
	
	@Autowired
	private UserCoinService userCoinService;
	
	@GetMapping("/user/{id}")
	public ResponseEntity<List<UserCoinDTO>> getUserByEmail(@PathVariable Integer id)
	{
		return ResponseEntity.ok(userCoinService.getUserCoinsByIdUser(id));
	}

}
