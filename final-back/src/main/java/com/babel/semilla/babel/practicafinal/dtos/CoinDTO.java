package com.babel.semilla.babel.practicafinal.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinDTO {
	
	private Integer id;
	private String name;
	private String symbol;
}
