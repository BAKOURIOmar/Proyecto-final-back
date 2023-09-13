package com.babel.semilla.babel.practicafinal.dtos;

import java.util.Date;
import java.util.List;

import com.babel.semilla.babel.practicafinal.entities.UserCoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String country;
    private String city;
    private String direccion;
    private String paymentMethod;
    private String numsTarjeta;
    private Date   dateOfExpiry;
    private String cvv;
    private String iban;
    private List<UserCoinDTO> userCoins;

    
}