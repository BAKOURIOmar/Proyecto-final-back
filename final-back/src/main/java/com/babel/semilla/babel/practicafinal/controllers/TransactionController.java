package com.babel.semilla.babel.practicafinal.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.babel.semilla.babel.practicafinal.dtos.TransactionDTO;
import com.babel.semilla.babel.practicafinal.entities.Transaction;
import com.babel.semilla.babel.practicafinal.entities.TransactionType;
import com.babel.semilla.babel.practicafinal.services.TransactionService;

@RestController
@RequestMapping("/Movimientos")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService ;
	
	
	@GetMapping("/filtrar")
    public ResponseEntity<List<TransactionDTO>> filtrarMovimientos(
            @RequestParam(name = "fecha", required = false)  Date fecha,
            @RequestParam(name = "moneda", required = false) String nameCoin,
            @RequestParam(name = "tipoOperacion", required = false) TransactionType tipoOperacion,
            @RequestParam(name = "email", required = true) String userEmail) {
        List<TransactionDTO> movimientos = transactionService.filtertransactions(fecha, nameCoin, tipoOperacion, userEmail);
        return ResponseEntity.ok(movimientos);
    }
	
	
}
