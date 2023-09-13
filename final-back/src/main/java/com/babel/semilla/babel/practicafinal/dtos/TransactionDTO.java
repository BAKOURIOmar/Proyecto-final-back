package com.babel.semilla.babel.practicafinal.dtos;

import java.util.Date;

import com.babel.semilla.babel.practicafinal.entities.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	private Integer id;
	
	private UserDTO user;
	private CoinDTO coin ;
	private Date date;
	private float amount;
	private TransactionType type;
}