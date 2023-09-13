package com.babel.semilla.babel.practicafinal.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
	
    private String paymentMethod;
    private String numsTarjeta;
    private Date dateOfExpiry;
    private String cvv;
    private String iban;
}
