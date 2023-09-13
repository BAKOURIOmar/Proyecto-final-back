package com.babel.semilla.babel.practicafinal.dtos;


import com.babel.semilla.babel.practicafinal.entities.Coin;
import com.babel.semilla.babel.practicafinal.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoinDTO {
	
	//		private Integer id;
	@JsonIgnore
	private UserDTO user;
	private CoinDTO coin;
	private Float quantity ;
	
	private Float marketPrice;
	
}
